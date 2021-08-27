package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.dvo.Idempotency;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyRepository extends PagingAndSortingRepository<Idempotency, String> {

}
