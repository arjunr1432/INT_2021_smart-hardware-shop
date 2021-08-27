package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.News;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.api.validator.NewsBusinessValidator;
import io.recruitment.assessment.gen.api.NewsApiDelegate;
import io.recruitment.assessment.gen.model.AddNewsRequest;
import io.recruitment.assessment.gen.model.NewsCatalogueResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.time.ZoneOffset.UTC;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsManagementServiceImpl implements NewsApiDelegate {

    private final NewsRepository newsRepository;

    private final NewsBusinessValidator businessValidator;


    @Override
    public CompletableFuture<ResponseEntity<NewsCatalogueResponseData>> newsApiV1AddPost(String idempotencyKey, AddNewsRequest addNewsRequest) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateNewsCreateRequest(idempotencyKey, addNewsRequest);
            log.info("Request validation completed for add new news information.");
            News news = newsRepository.save(
                    News.builder()
                            .title(addNewsRequest.getTitle())
                            .description(addNewsRequest.getDescription())
                            .expiryDate(Timestamp.valueOf(addNewsRequest.getExpiryDate().toLocalDateTime()))
                            .build());
            log.info("Saved data to database successfully.");

            return  new ResponseEntity<NewsCatalogueResponseData>(
                    new NewsCatalogueResponseData()
                            .newsId(news.getId())
                            .title(news.getTitle())
                            .description(news.getDescription())
                            .expiryDate(news.getExpiryDate().toLocalDateTime().atOffset(UTC)),
                    HttpStatus.CREATED);
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<List<NewsCatalogueResponseData>>> newsApiV1ListGet(Long pageNo, Long pageSize) {
        return CompletableFuture.supplyAsync(() -> {
            businessValidator.validateGetNewsListRequest(pageNo, pageSize);
            List<NewsCatalogueResponseData> dataList = new ArrayList<>();
            newsRepository.findAll(
                    PageRequest.of(
                            pageNo == null ? 0 : pageNo.intValue(),
                            pageSize == null ? 10 : pageSize.intValue(),
                            Sort.by("id")))
                    .forEach(news -> {
                        dataList.add(new NewsCatalogueResponseData()
                        .newsId(news.getId())
                        .title(news.getTitle())
                        .description(news.getDescription())
                        .expiryDate(news.getExpiryDate().toLocalDateTime().atOffset(UTC)));
            });
            log.info("Fetched all data from database successfully, pageNo={}, pageSize={}", pageNo, pageSize);

            return  new ResponseEntity<List<NewsCatalogueResponseData>>(dataList, HttpStatus.OK);
        });
    }

}
