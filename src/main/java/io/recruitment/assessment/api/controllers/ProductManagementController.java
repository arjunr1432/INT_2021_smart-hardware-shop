package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.gen.api.ProductsApi;
import io.recruitment.assessment.gen.api.ProductsApiDelegate;
import io.recruitment.assessment.gen.model.CreateOrUpdateProductRequest;
import io.recruitment.assessment.gen.model.ProductCatalogueResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ProductManagementController implements ProductsApi {

    @Autowired
    private ProductsApiDelegate productsApiDelegate;

    /**
     * POST /products/api/v1/add : This API will add a product to the catalogue.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param createOrUpdateProductRequest Request payload for adding a new product to our catalogue. (required)
     * @return Successful response: created product information. (status code 201)
     *         or Exception scenarios (status code 200)
     * @see ProductsApi#productsApiV1AddPost
     */
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1AddPost(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,
            @ApiParam(value = "Request payload for adding a new product to our catalogue." ,required=true )
            @Valid @RequestBody CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return productsApiDelegate.productsApiV1AddPost(idempotencyKey, createOrUpdateProductRequest);
    }

    /**
     * DELETE /products/api/v1/delete/{productId} : This API will delete an existing product from the catalogue.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param productId The unique product id in our system, for which we need to delete from our system. (required)
     * @return Successful response: deleted product information. (status code 200)
     *         or Exception scenarios (status code 200)
     * @see ProductsApi#productsApiV1DeleteProductIdDelete
     */
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1DeleteProductIdDelete(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,
            @ApiParam(value = "The unique product id in our system, for which we need to delete from our system.",required=true)
            @PathVariable("productId") Long productId) {
        return productsApiDelegate.productsApiV1DeleteProductIdDelete(idempotencyKey, productId);
    }

    /**
     * GET /products/api/v1/list : This API will return the Products list in the catalogue.
     *
     * @param searchParam Custom search param for filtering the product list. (optional)
     * @param pageNo Page number to choose which page to list, by default the value will be 1. (optional)
     * @param pageSize Page size to choose how many entries to be shown per page, by default the value will be 10. (optional)
     * @return Successful response: sub list of product catalogue. (status code 200)
     *         or Exception scenarios (status code 200)
     * @see ProductsApi#productsApiV1ListGet
     */
    public CompletableFuture<ResponseEntity<List<ProductCatalogueResponseData>>> productsApiV1ListGet(
            @ApiParam(value = "Custom search param for filtering the product list.")
            @Valid @RequestParam(value = "searchParam", required = false) String searchParam,
            @ApiParam(value = "Page number to choose which page to list, by default the value will be 1.")
            @Valid @RequestParam(value = "pageNo", required = false) Long pageNo,
            @ApiParam(value = "Page size to choose how many entries to be shown per page, by default the value will be 10.")
            @Valid @RequestParam(value = "pageSize", required = false) Long pageSize) {
        return productsApiDelegate.productsApiV1ListGet(searchParam, pageNo, pageSize);
    }

    /**
     * PUT /products/api/v1/update/{productId} : This API will update an existing product from the catalogue.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param productId The unique product id in our system, for which we need to update informations. (required)
     * @param createOrUpdateProductRequest Request payload for updating an existing product in our catalogue.. (required)
     * @return Successful response: created product information. (status code 200)
     *         or Exception scenarios
     * @see ProductsApi#productsApiV1UpdateProductIdPut
     */
    public CompletableFuture<ResponseEntity<ProductCatalogueResponseData>> productsApiV1UpdateProductIdPut(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,
            @ApiParam(value = "The unique product id in our system, for which we need to update informations.",required=true)
            @PathVariable("productId") Long productId,
            @ApiParam(value = "Request payload for updating an existing product in our catalogue.." ,required=true )
            @Valid @RequestBody CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        return productsApiDelegate.productsApiV1UpdateProductIdPut(idempotencyKey, productId, createOrUpdateProductRequest);
    }
}
