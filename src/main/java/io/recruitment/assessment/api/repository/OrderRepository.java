package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.dvo.OrderDetails;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<OrderDetails, String> {

}
