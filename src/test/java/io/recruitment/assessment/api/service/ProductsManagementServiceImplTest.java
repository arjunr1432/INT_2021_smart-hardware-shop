package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.News;
import io.recruitment.assessment.api.dvo.Products;
import io.recruitment.assessment.api.repository.ProductRepository;
import io.recruitment.assessment.api.validator.ProductsBusinessValidator;
import io.recruitment.assessment.gen.api.ProductsApiDelegate;
import io.recruitment.assessment.gen.model.AddNewsRequest;
import io.recruitment.assessment.gen.model.CreateOrUpdateProductRequest;
import io.recruitment.assessment.gen.model.NewsCatalogueResponseData;
import io.recruitment.assessment.gen.model.ProductCatalogueResponseData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductsManagementServiceImplTest {

    private ProductRepository productRepository;

    private ProductsBusinessValidator businessValidator;

    private ProductsApiDelegate productsApiDelegate;

    @BeforeAll
    public void init() {
        productRepository = Mockito.mock(ProductRepository.class);
        businessValidator = Mockito.mock(ProductsBusinessValidator.class);
        productsApiDelegate = new ProductsManagementServiceImpl(productRepository, businessValidator);
    }

    @Test
    void testProductsApiV1AddPost_Success() {
        Mockito.doNothing().when(businessValidator).validateProductCreateRequest(Mockito.anyString());
        Mockito.when(productRepository.save(Mockito.any(Products.class)))
                .thenReturn(Products.builder()
                        .id(1234L)
                        .name("Product Name")
                        .description("Product Description")
                        .price("100.00").build());

        productsApiDelegate.productsApiV1AddPost("IdemPotencyKey", new CreateOrUpdateProductRequest())
                .thenApply(productCatalogueResponseDataResponseEntity -> {
                    ProductCatalogueResponseData productCatalogueResponseData = productCatalogueResponseDataResponseEntity.getBody();
                    Assertions.assertEquals(1234L, productCatalogueResponseData.getProductId());
                    Assertions.assertEquals("Product Name", productCatalogueResponseData.getName());
                    Assertions.assertEquals("Product Description", productCatalogueResponseData.getDescription());
                    Assertions.assertEquals("100.00", productCatalogueResponseData.getPrice());
                    return productCatalogueResponseDataResponseEntity;
                });

    }

    @Test
    void testProductsApiV1DeleteProductIdDelete_Success() {
        Mockito.doNothing().when(businessValidator).validateProductDeleteRequest(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(productRepository.findById(Mockito.any(Long.class)))
                .thenReturn(Optional.of(Products.builder()
                        .id(1234L)
                        .name("Product Name")
                        .description("Product Description")
                        .price("100.00").build()));
        Mockito.doNothing().when(productRepository).deleteById(Mockito.anyLong());

        productsApiDelegate.productsApiV1DeleteProductIdDelete("IdemPotencyKey", 1234L)
                .thenApply(productCatalogueResponseDataResponseEntity -> {
                    ProductCatalogueResponseData productCatalogueResponseData = productCatalogueResponseDataResponseEntity.getBody();
                    Assertions.assertEquals(1234L, productCatalogueResponseData.getProductId());
                    Assertions.assertEquals("Product Name", productCatalogueResponseData.getName());
                    Assertions.assertEquals("Product Description", productCatalogueResponseData.getDescription());
                    Assertions.assertEquals("100.00", productCatalogueResponseData.getPrice());
                    return productCatalogueResponseDataResponseEntity;
                });
    }

    @Test
    void productsApiV1ListGet_Success_WithSearchParam() {
        List<Products> newsList = new ArrayList<>();
        newsList.add(Products.builder().id(1234L).name("Product Name").description("Product Description").price("100.00").build());
        Mockito.doNothing().when(businessValidator).validateGetProductLiseRequest(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(productRepository.findByNameContaining(Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(newsList));
        productsApiDelegate.productsApiV1ListGet("searchParam",1L, 2L)
                .thenApply(listResponseEntity -> {
                    List<ProductCatalogueResponseData> dataList = listResponseEntity.getBody();
                    Assertions.assertEquals(1, dataList.size());

                    return listResponseEntity;
                });
    }

    @Test
    void productsApiV1ListGet_Success_WithOutSearchParam() {
        List<Products> newsList = new ArrayList<>();
        newsList.add(Products.builder().id(1234L).name("Product Name").description("Product Description").price("100.00").build());
        newsList.add(Products.builder().id(1234L).name("Product Name").description("Product Description").price("100.00").build());
        newsList.add(Products.builder().id(1234L).name("Product Name").description("Product Description").price("100.00").build());
        Mockito.doNothing().when(businessValidator).validateGetProductLiseRequest(Mockito.anyLong(), Mockito.anyLong());
        Mockito.when(productRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(newsList));

        productsApiDelegate.productsApiV1ListGet("",1L, 2L)
                .thenApply(listResponseEntity -> {
                    List<ProductCatalogueResponseData> dataList = listResponseEntity.getBody();
                    Assertions.assertEquals(3, dataList.size());

                    return listResponseEntity;
                });
    }

    @Test
    void testProductsApiV1UpdateProductIdPut_Success() {
        Mockito.doNothing().when(businessValidator).validateProductUpdateRequest(Mockito.anyString(), Mockito.anyLong());
        Mockito.when(productRepository.save(Mockito.any(Products.class)))
                .thenReturn(Products.builder()
                        .id(1234L)
                        .name("Product Name")
                        .description("Product Description")
                        .price("100.00").build());

        productsApiDelegate.productsApiV1UpdateProductIdPut("IdemPotencyKey", 1234L, new CreateOrUpdateProductRequest())
                .thenApply(productCatalogueResponseDataResponseEntity -> {
                    ProductCatalogueResponseData productCatalogueResponseData = productCatalogueResponseDataResponseEntity.getBody();
                    Assertions.assertEquals(1234L, productCatalogueResponseData.getProductId());
                    Assertions.assertEquals("Product Name", productCatalogueResponseData.getName());
                    Assertions.assertEquals("Product Description", productCatalogueResponseData.getDescription());
                    Assertions.assertEquals("100.00", productCatalogueResponseData.getPrice());
                    return productCatalogueResponseDataResponseEntity;
                });
    }
}