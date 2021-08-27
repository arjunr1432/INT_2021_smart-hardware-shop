package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.dvo.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Products, Long> {

    Page<Products> findByNameContaining(String name, Pageable var1);


}
