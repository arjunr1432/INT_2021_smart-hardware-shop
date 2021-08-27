package io.recruitment.assessment.api.service;

import io.recruitment.assessment.api.dvo.News;
import io.recruitment.assessment.api.exception.CustomBusinessException;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.api.validator.NewsBusinessValidator;
import io.recruitment.assessment.gen.api.NewsApiDelegate;
import io.recruitment.assessment.gen.model.AddNewsRequest;
import io.recruitment.assessment.gen.model.NewsCatalogueResponseData;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewsManagementServiceImplTest {

    private NewsRepository newsRepository;

    private NewsBusinessValidator businessValidator;

    private NewsApiDelegate newsApiDelegate;


    @BeforeAll
    public void init() {
        newsRepository = Mockito.mock(NewsRepository.class);
        businessValidator = Mockito.mock(NewsBusinessValidator.class);
        newsApiDelegate = new NewsManagementServiceImpl(newsRepository, businessValidator);
    }

    @Test
    void testNewsApiV1AddPost_Success() {
        Mockito.doNothing().when(businessValidator).validateNewsCreateRequest(
                Mockito.anyString(), Mockito.any(AddNewsRequest.class));
        Mockito.when(newsRepository.save(Mockito.any(News.class)))
                .thenReturn(News.builder().id(1234L).title("News Title").description("News Description").build());

        newsApiDelegate.newsApiV1AddPost("idempotencyKey", new AddNewsRequest())
                .thenApply(newsCatalogueResponseDataResponseEntity -> {
                    NewsCatalogueResponseData newsCatalogueResponseData = newsCatalogueResponseDataResponseEntity.getBody();
                    Assertions.assertEquals(1234L, newsCatalogueResponseData.getNewsId());
                    Assertions.assertEquals("News Title", newsCatalogueResponseData.getTitle());
                    Assertions.assertEquals("News Description", newsCatalogueResponseData.getDescription());

                    return newsCatalogueResponseData;
        });
    }

    @Test
    void testNewsApiV1AddPost_Failed_ValidationError() {
        Mockito.doThrow(new CustomBusinessException(HttpStatus.BAD_REQUEST, "BadRequest", "Request contains invalid details"))
                .when(businessValidator).validateNewsCreateRequest(
                Mockito.anyString(), Mockito.any(AddNewsRequest.class));
        try {
            newsApiDelegate.newsApiV1AddPost("idempotencyKey", new AddNewsRequest());
        } catch (Exception e) {
            Assertions.assertEquals("Request contains invalid details", e.getMessage());
        }

    }

    @Test
    void testNewsApiV1ListGet_Success() {
        List<News> newsList = new ArrayList<>();
        newsList.add(News.builder().id(1234L).title("News Title").description("News Description").build());
        newsList.add(News.builder().id(1234L).title("News Title").description("News Description").build());
        Mockito.doNothing().when(businessValidator).validateNewsCreateRequest(
                Mockito.anyString(), Mockito.any(AddNewsRequest.class));
        Mockito.when(newsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(newsList));

        newsApiDelegate.newsApiV1ListGet(1L, 2L)
                .thenApply(listResponseEntity -> {
                    List<NewsCatalogueResponseData> dataList = listResponseEntity.getBody();
                    Assertions.assertEquals(2, dataList.size());

                    return listResponseEntity;
                });
    }

    @Test
    void testNewsApiV1ListGet_Success_NoRecords() {
        List<News> newsList = new ArrayList<>();
        Mockito.doNothing().when(businessValidator).validateNewsCreateRequest(
                Mockito.anyString(), Mockito.any(AddNewsRequest.class));
        Mockito.when(newsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(newsList));

        newsApiDelegate.newsApiV1ListGet(1L, 2L)
                .thenApply(listResponseEntity -> {
                    List<NewsCatalogueResponseData> dataList = listResponseEntity.getBody();
                    Assertions.assertEquals(0, dataList.size());

                    return listResponseEntity;
                });
    }
}