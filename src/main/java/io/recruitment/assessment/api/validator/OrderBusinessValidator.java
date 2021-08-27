package io.recruitment.assessment.api.validator;

import io.recruitment.assessment.api.dvo.Idempotency;
import io.recruitment.assessment.api.exception.CustomBusinessException;
import io.recruitment.assessment.api.repository.IdempotencyRepository;
import io.recruitment.assessment.api.repository.OrderRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import io.recruitment.assessment.gen.model.AddProductRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderBusinessValidator {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final IdempotencyRepository idempotencyRepository;



    private void validateIdempotencyKey(String idempotencyKey) {
        if (StringUtils.isBlank(idempotencyKey)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Invalid Idempotency Key, should not be empty.");
        }
        /* Validate idempotency key and insert as required.*/
        if (idempotencyRepository.existsById(idempotencyKey)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Invalid Idempotency Key, already exists.");
        } else {
            idempotencyRepository.save(Idempotency.builder().id(idempotencyKey).build());
        }
    }



    public void validateOrderCreateRequest(String idempotencyKey) {
        validateIdempotencyKey(idempotencyKey);
    }

    public void validateOrderAddProductRequest(String idempotencyKey, String orderId, AddProductRequest addProductRequest) {
        validateIdempotencyKey(idempotencyKey);
        if (!orderRepository.existsById(orderId)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Invalid Order id, requested order id not exists in our system.");
        }
        if (!productRepository.existsById(addProductRequest.getProductId())) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Invalid Product id, requested product id not exists in our system.");
        }
        if (addProductRequest.getCount() < 0) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Invalid item count, should be a positive number.");
        }
    }

    public void validateGetOrderSummaryRequest(String orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Invalid Order id, requested order id not exists in our system.");
        }
    }
}
