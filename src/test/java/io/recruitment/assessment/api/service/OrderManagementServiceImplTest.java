package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.OrderDetails;
import io.recruitment.assessment.api.dvo.ProductItem;
import io.recruitment.assessment.api.repository.OrderRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import io.recruitment.assessment.api.validator.OrderBusinessValidator;
import io.recruitment.assessment.gen.api.OrdersApiDelegate;
import io.recruitment.assessment.gen.model.ShoppingCartData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderManagementServiceImplTest {

    private OrderRepository orderRepository;

    private ProductRepository productRepository;

    private OrderBusinessValidator businessValidator;

    private OrdersApiDelegate ordersApiDelegate;

    @BeforeAll
    public void init() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        businessValidator = Mockito.mock(OrderBusinessValidator.class);
        ordersApiDelegate = new OrderManagementServiceImpl(orderRepository, productRepository, businessValidator);
    }

    @Test
    void testOrdersApiV1CreatePost_Success() {
        Mockito.doNothing().when(businessValidator).validateOrderCreateRequest(Mockito.anyString());
        Mockito.when(orderRepository.save(Mockito.any(OrderDetails.class)))
                .thenReturn(OrderDetails.builder()
                        .id("orderId")
                        .createDate(Timestamp.valueOf(LocalDateTime.now()))
                        .productItems(new ArrayList<>())
                        .build());

        ordersApiDelegate.ordersApiV1CreatePost("IdemPotencyKey")
                .thenApply(responseEntity -> {
                    ShoppingCartData data = responseEntity.getBody();
                    Assertions.assertEquals("orderId", data.getOrderId());
                    Assertions.assertEquals(0, data.getItems().size());
                    return responseEntity;
                });
    }

    @Test
    void testOrdersApiV1SummaryOrderIdGet_Success() {
        List<ProductItem> productItems = new ArrayList<>();
        productItems.add(ProductItem.builder().productId(1234L).name("Product1").count(10L).price("100.00").build());
        productItems.add(ProductItem.builder().productId(2345L).name("Product1").count(10L).price("100.00").build());
        Mockito.doNothing().when(businessValidator).validateGetOrderSummaryRequest(Mockito.anyString());
        Mockito.when(orderRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(OrderDetails.builder()
                        .id("orderId")
                        .createDate(Timestamp.valueOf(LocalDateTime.now()))
                        .productItems(productItems)
                        .build()));

        ordersApiDelegate.ordersApiV1SummaryOrderIdGet("orderId")
                .thenApply(responseEntity -> {
                    ShoppingCartData data = responseEntity.getBody();
                    Assertions.assertEquals("orderId", data.getOrderId());
                    Assertions.assertEquals(2, data.getItems().size());
                    return responseEntity;
        });
    }
}