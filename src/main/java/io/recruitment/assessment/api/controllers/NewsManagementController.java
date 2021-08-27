package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.gen.api.NewsApi;
import io.recruitment.assessment.gen.api.NewsApiDelegate;
import io.recruitment.assessment.gen.model.AddNewsRequest;
import io.recruitment.assessment.gen.model.NewsCatalogueResponseData;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NewsManagementController implements NewsApi {

    private final NewsApiDelegate newsApiDelegate;

    /**
     * POST /news/api/v1/add : This API will add a news or product offer to our system.
     *
     * @param idempotencyKey Unique identifier for idempotency (required)
     * @param addNewsRequest Request payload for adding a new news or product offer to our system. (required)
     * @return Successful response: created news information. (status code 201)
     *         or Exception scenarios (status code 200)
     * @see NewsApi#newsApiV1AddPost
     */
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<NewsCatalogueResponseData>> newsApiV1AddPost(
            @ApiParam(value = "Unique identifier for idempotency" ,required=true)
            @RequestHeader(value="Idempotency-Key", required=true) String idempotencyKey,
            @ApiParam(value = "Request payload for adding a new news or product offer to our system." ,required=true )
            @Valid @RequestBody AddNewsRequest addNewsRequest) {
        log.info("Request received for adding news, idempotencyKey={}", idempotencyKey);

        return newsApiDelegate.newsApiV1AddPost(idempotencyKey, addNewsRequest)
                .thenApply(newsCatalogueResponseDataResponseEntity -> {
                    log.info("News added successfully.");
                    return newsCatalogueResponseDataResponseEntity;
        });
    }

    /**
     * GET /news/api/v1/list : This API will return the Products list in the catalogue.
     *
     * @param pageNo Page number to choose which page to list, by default the value will be 0. (optional)
     * @param pageSize Page size to choose how many entries to be shown per page, by default the value will be 10. (optional)
     * @return Successful response: List of all active newses or offers. (status code 200)
     *         or Exception scenarios (status code 200)
     * @see NewsApi#newsApiV1ListGet
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public CompletableFuture<ResponseEntity<List<NewsCatalogueResponseData>>> newsApiV1ListGet(
            @ApiParam(value = "Page number to choose which page to list, by default the value will be 0.")
            @Valid @RequestParam(value = "pageNo", required = false) Long pageNo,
            @ApiParam(value = "Page size to choose how many entries to be shown per page, by default the value will be 10.")
            @Valid @RequestParam(value = "pageSize", required = false) Long pageSize) {
        log.info("Request received for fetching news list, pageSize={}, pageNo={}", pageSize, pageNo);

        return newsApiDelegate.newsApiV1ListGet(pageNo, pageSize)
                .thenApply(listResponseEntity -> {
                    log.info("Fetched {} records successfully.", listResponseEntity.getBody().size());
                    return listResponseEntity;
        });
    }
}
