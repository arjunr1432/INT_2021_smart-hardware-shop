package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.dvo.ProductItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends PagingAndSortingRepository<ProductItem, Long> {

}
