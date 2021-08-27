package io.recruitment.assessment.api.validator;

import io.recruitment.assessment.api.dvo.Idempotency;
import io.recruitment.assessment.api.exception.CustomBusinessException;
import io.recruitment.assessment.api.repository.IdempotencyRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductsBusinessValidator {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    public void validateProductCreateRequest(String idempotencyKey){
        validateIdempotencyKey(idempotencyKey);
    }

    public void validateProductDeleteRequest(String idempotencyKey, Long productId) {
        validateIdempotencyKey(idempotencyKey);
        if (!productRepository.existsById(productId)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Requested product not exists in our system.");
        }
    }

    public void validateProductUpdateRequest(String idempotencyKey, Long productId) {
        validateProductCreateRequest(idempotencyKey);
        if (!productRepository.existsById(productId)) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "BusinessValidationError", "Requested product not exists in our system.");
        }
    }

    public void validateGetProductLiseRequest(Long pageNo, Long pageSize) {
        if (pageNo != null && pageNo < 0) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Validation error : pageNo should be a valid positive number.");
        }

        if (pageSize != null && pageSize < 0) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Validation error : pageSize should be a valid positive number.");
        }
    }

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



}
