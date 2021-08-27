package io.recruitment.assessment.api.validator;

import io.recruitment.assessment.api.dvo.Idempotency;
import io.recruitment.assessment.api.exception.CustomBusinessException;
import io.recruitment.assessment.api.repository.IdempotencyRepository;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.gen.model.AddNewsRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class NewsBusinessValidator {

    private final NewsRepository productRepository;

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


    public void validateNewsCreateRequest(String idempotencyKey, AddNewsRequest addNewsRequest) {
        validateIdempotencyKey(idempotencyKey);
        if (addNewsRequest.getExpiryDate().isBefore(OffsetDateTime.now())) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Invalid News expiryDate, should be a valid future date.");
        }
    }

    public void validateGetNewsListRequest(Long pageNo, Long pageSize) {
        if (pageNo != null && pageNo < 0) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Validation error : pageNo should be a valid positive number.");
        }

        if (pageSize != null && pageSize < 0) {
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, "DataValidationError", "Validation error : pageSize should be a valid positive number.");
        }
    }
}
