package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.OrderDetails;
import io.recruitment.assessment.api.dvo.ProductItem;
import io.recruitment.assessment.api.dvo.Products;
import io.recruitment.assessment.api.repository.OrderRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import io.recruitment.assessment.api.validator.OrderBusinessValidator;
import io.recruitment.assessment.gen.api.OrdersApiDelegate;
import io.recruitment.assessment.gen.model.AddProductRequest;
import io.recruitment.assessment.gen.model.ProductItemData;
import io.recruitment.assessment.gen.model.ShoppingCartData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
public class OrderManagementServiceImpl implements OrdersApiDelegate {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderBusinessValidator businessValidator;


    @Override
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1CreatePost(String idempotencyKey) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateOrderCreateRequest(idempotencyKey);
            log.info("Request validation completed for create a new order.");
            OrderDetails orderDetails = orderRepository.save(OrderDetails.builder()
                    .id(UUID.randomUUID().toString())
                    .createDate(Timestamp.valueOf(OffsetDateTime.now().toLocalDateTime())).build());
            log.info("Saved data to database successfully.");

            return new ResponseEntity<ShoppingCartData>(new ShoppingCartData()
                    .orderId(orderDetails.getId())
                    .createdDate(orderDetails.getCreateDate().toLocalDateTime().atOffset(UTC)), HttpStatus.CREATED);
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1AddOrderIdPut(String idempotencyKey, String orderId, AddProductRequest addProductRequest) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateOrderAddProductRequest(idempotencyKey, orderId, addProductRequest);
            log.info("Request validation completed for add new product to existing order.");
            OrderDetails orderDetails = orderRepository.findById(orderId).get();
            log.info("Fetched order details from database.");
            AtomicBoolean flag = new AtomicBoolean(false);
            orderDetails.getProductItems().forEach(productItem -> {
                Products products = productRepository.findById(addProductRequest.getProductId()).get();
                if (productItem.getProductId().equals(products.getId())) {
                    log.info("Product already exists in order, incrementing the item count by {}", addProductRequest.getCount());
                    productItem.setCount(productItem.getCount() + addProductRequest.getCount());
                    flag.set(true);
                }
            });
            if (!flag.get()) {
                log.info("New product being added to the order list. productId={}", addProductRequest.getProductId());
                Products products = productRepository.findById(addProductRequest.getProductId()).get();

                orderDetails.getProductItems().add(
                        ProductItem.builder()
                                .productId(products.getId())
                                .name(products.getName())
                                .price(products.getPrice())
                                .count(addProductRequest.getCount())
                                .build());
            }

            orderRepository.save(orderDetails);
            log.info("Saved data to database successfully.");

            List<ProductItemData> items = new ArrayList<>();
            final BigDecimal[] totalPrice = {BigDecimal.ZERO};
            orderDetails.getProductItems().forEach(productItem -> {
                BigDecimal price = new BigDecimal(productItem.getPrice());
                BigDecimal totalItemPrice = price.multiply(BigDecimal.valueOf(productItem.getCount()));
                totalPrice[0] = totalPrice[0].add(totalItemPrice);
                items.add(new ProductItemData()
                        .productId(productItem.getProductId())
                        .name(productItem.getName())
                        .price(productItem.getPrice())
                        .count(productItem.getCount())
                        .totalItemPrice(String.valueOf(totalItemPrice)));
            });
            log.info("Calculated the total price for the order, totalPrice={}", totalPrice[0]);
            return new ResponseEntity<ShoppingCartData>(new ShoppingCartData()
                    .orderId(orderDetails.getId())
                    .createdDate(orderDetails.getCreateDate().toLocalDateTime().atOffset(UTC))
                    .items(items).totalPrice(String.valueOf(totalPrice[0])),
                    HttpStatus.CREATED);
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<ShoppingCartData>> ordersApiV1SummaryOrderIdGet(String orderId) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateGetOrderSummaryRequest(orderId);
            log.info("Request validation completed for get order summary.");
            OrderDetails orderDetails = orderRepository.findById(orderId).get();

            List<ProductItemData> items = new ArrayList<>();
            final BigDecimal[] totalPrice = {BigDecimal.ZERO};
            orderDetails.getProductItems().forEach(productItem -> {
                BigDecimal price = new BigDecimal(productItem.getPrice());
                BigDecimal totalItemPrice = price.multiply(BigDecimal.valueOf(productItem.getCount()));
                totalPrice[0] = totalPrice[0].add(totalItemPrice);
                items.add(new ProductItemData()
                        .productId(productItem.getProductId())
                        .name(productItem.getName())
                        .price(productItem.getPrice())
                        .count(productItem.getCount())
                        .totalItemPrice(String.valueOf(totalItemPrice)));
            });
            log.info("Calculated the total price for the order, totalPrice={}", totalPrice[0]);
            return new ResponseEntity<ShoppingCartData>(new ShoppingCartData()
                    .orderId(orderDetails.getId())
                    .createdDate(orderDetails.getCreateDate().toLocalDateTime().atOffset(UTC))
                    .items(items).totalPrice(String.valueOf(totalPrice[0])),
                    HttpStatus.OK);
        });
    }
}
