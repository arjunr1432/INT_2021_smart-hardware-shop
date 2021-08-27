package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.Products;
import io.recruitment.assessment.api.repository.ProductRepository;
import io.recruitment.assessment.api.validator.ProductsBusinessValidator;
import io.recruitment.assessment.gen.api.ProductsApiDelegate;
import io.recruitment.assessment.gen.model.CreateOrUpdateProductRequest;
import io.recruitment.assessment.gen.model.ProductCatalogueResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsManagementServiceImpl implements ProductsApiDelegate {

    private final ProductRepository productRepository;

    private final ProductsBusinessValidator businessValidator;

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1AddPost(String idempotencyKey, CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateProductCreateRequest(idempotencyKey);
            log.info("Request validation completed for add new product information.");
            Products products = productRepository.save(Products.builder()
                    .name(createOrUpdateProductRequest.getName())
                    .description(createOrUpdateProductRequest.getDescription())
                    .price(createOrUpdateProductRequest.getPrice())
                    .build());
            log.info("Saved data to database successfully.");

            return  new ResponseEntity<ProductCatalogueResponseData>(
                    new ProductCatalogueResponseData()
                            .productId(products.getId())
                            .name(products.getName())
                            .description(products.getDescription())
                            .price(products.getPrice()),
                    HttpStatus.CREATED);
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1DeleteProductIdDelete(String idempotencyKey, Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateProductDeleteRequest(idempotencyKey, productId);
            log.info("Request validation completed for delete existing product information.");
            Products products = productRepository.findById(productId).get();
            productRepository.deleteById(productId);
            log.info("Deleted data from database successfully.");

            return  new ResponseEntity<ProductCatalogueResponseData>(
                    new ProductCatalogueResponseData()
                            .productId(products.getId())
                            .name(products.getName())
                            .description(products.getDescription())
                            .price(products.getPrice()),
                    HttpStatus.OK);
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<List<ProductCatalogueResponseData>>> productsApiV1ListGet(String searchParam, Long pageNo, Long pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateGetProductLiseRequest(pageNo, pageSize);
            log.info("Request validation completed for ");
            List<ProductCatalogueResponseData> dataList = new ArrayList<>();
            Page<Products> page;
            if (StringUtils.isBlank(searchParam)) {
                page = productRepository.findAll(
                        PageRequest.of(
                                pageNo == null ? 0 : pageNo.intValue(),
                                pageSize == null ? 10 : pageSize.intValue(),
                                Sort.by("id")));
                log.info("Fetched all data from database successfully, pageNo={}, pageSize={}", pageNo, pageSize);
            } else {
                page = productRepository.findByNameContaining(searchParam,
                        PageRequest.of(
                                pageNo == null ? 0 : pageNo.intValue(),
                                pageSize == null ? 10 : pageSize.intValue(),
                                Sort.by("id")));
                log.info("Fetched specific data from database successfully, searchParam={}, pageNo={}, pageSize={}",
                        searchParam, pageNo, pageSize);
            }

            page.forEach(products -> {
                dataList.add(new ProductCatalogueResponseData()
                        .productId(products.getId())
                        .name(products.getName())
                        .description(products.getDescription())
                        .price(products.getPrice()));
            });
            return  new ResponseEntity<List<ProductCatalogueResponseData>>(dataList
                    , HttpStatus.OK);

        });
    }

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1UpdateProductIdPut(String idempotencyKey, Long productId, CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateProductUpdateRequest(idempotencyKey, productId);
            log.info("Request validation completed for ");
            Products products = productRepository.save(Products.builder()
                    .id(productId)
                    .name(createOrUpdateProductRequest.getName())
                    .description(createOrUpdateProductRequest.getDescription())
                    .price(createOrUpdateProductRequest.getPrice())
                    .build());
            log.info("Updated data for productId={} in database successfully.", productId);

            return  new ResponseEntity<ProductCatalogueResponseData>(
                    new ProductCatalogueResponseData()
                            .productId(products.getId())
                            .name(products.getName())
                            .description(products.getDescription())
                            .price(products.getPrice()),
                    HttpStatus.OK);
        });
    }
}
