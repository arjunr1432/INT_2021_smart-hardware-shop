package io.recruitment.assessment.api.service;

import io.recruitment.assessment.gen.api.ProductsApiDelegate;
import io.recruitment.assessment.gen.model.CreateOrUpdateProductRequest;
import io.recruitment.assessment.gen.model.ProductCatalogueResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductsManagementServiceImpl implements ProductsApiDelegate {

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1AddPost(String idempotencyKey, CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1DeleteProductIdDelete(String idempotencyKey, Long productId) {
        return null;
    }

    @Override
    public CompletableFuture<ResponseEntity<List<ProductCatalogueResponseData>>> productsApiV1ListGet(String searchParam, Long pageNo, Long pageSize) {
        ResponseEntity responseEntity = new ResponseEntity<List<ProductCatalogueResponseData>>(Arrays.asList(new ProductCatalogueResponseData().productId(12345L)), HttpStatus.OK);
        return CompletableFuture.completedFuture(responseEntity);
    }

    @Override
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1UpdateProductIdPut(String idempotencyKey, Long productId, CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return null;
    }
}
