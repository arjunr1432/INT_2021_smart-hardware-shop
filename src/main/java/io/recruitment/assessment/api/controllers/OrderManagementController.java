package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.gen.api.OrdersApi;
import io.recruitment.assessment.gen.api.OrdersApiDelegate;
import io.recruitment.assessment.gen.model.AddProductRequest;
import io.recruitment.assessment.gen.model.ShoppingCartData;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderManagementController implements OrdersApi {

    private final OrdersApiDelegate ordersApiDelegate;

    /**
     * POST /orders/api/v1/create : This API will create a new shopping cart for the customer to which the products will be added for checkout.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @return Successful response: created new shopping cart with unique order id. (status code 201)
     *         or Exception scenarios (status code 200)
     * @see OrdersApi#ordersApiV1CreatePost
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1CreatePost(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey) {
        log.info("Request received for creating a new order, idempotencyKey={}", idempotencyKey);
        return ordersApiDelegate.ordersApiV1CreatePost(idempotencyKey)
                .thenApply(shoppingCartDataResponseEntity -> {
                    log.info("Order created successfully with orderId={}", shoppingCartDataResponseEntity.getBody().getOrderId());
                    return shoppingCartDataResponseEntity;
        });
    }

    /**
     * PUT /orders/api/v1/add/{orderId} : This API will add a new product to an existing shopping cart.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param orderId The unique order id in our system, for which we will be adding new product. (required)
     * @param addProductRequest Request payload for adding a new news or product offer to our system. (required)
     * @return Successful response: created product information. (status code 200)
     *         or Exception scenarios (status code 200)
     * @see OrdersApi#ordersApiV1AddOrderIdPut
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1AddOrderIdPut(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,
            @ApiParam(value = "The unique order id in our system, for which we will be adding new product.",required=true)
            @PathVariable("orderId") String orderId,
            @ApiParam(value = "Request payload for adding a new news or product offer to our system." ,required=true )
            @Valid @RequestBody AddProductRequest addProductRequest) {
        log.info("Request received for adding productId={} to the orderId={}, idempotencyKey={}",
                addProductRequest.getProductId(), orderId, idempotencyKey);
        return ordersApiDelegate.ordersApiV1AddOrderIdPut(idempotencyKey, orderId, addProductRequest)
                .thenApply(shoppingCartDataResponseEntity -> {
                    log.info("Product has been added successfully to the orderId={}", orderId);
                    return shoppingCartDataResponseEntity;
        });
    }

    /**
     * GET /orders/api/v1/summary/{orderId} : This API will show the summary of a shopping cart.
     *
     * @param orderId  (required)
     * @return Successful response: created product information. (status code 200)
     *         or Exception scenarios (status code 200)
     * @see OrdersApi#ordersApiV1SummaryOrderIdGet
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1SummaryOrderIdGet(
            @ApiParam(value = "",required=true) @PathVariable("orderId") String orderId) {
        log.info("Request received for get order summary, orderId={}", orderId);
        return ordersApiDelegate.ordersApiV1SummaryOrderIdGet(orderId)
                .thenApply(shoppingCartDataResponseEntity -> {
                    log.info("Order summary has been generated and returned for orderId={}", orderId);
                    return shoppingCartDataResponseEntity;
        });
    }
}
