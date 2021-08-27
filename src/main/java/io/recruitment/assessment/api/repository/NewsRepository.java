package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.dvo.News;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<News, Long> {

}
