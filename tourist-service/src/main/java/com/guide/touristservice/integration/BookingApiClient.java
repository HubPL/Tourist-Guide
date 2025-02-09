package com.guide.touristservice.integration;

import com.guide.touristservice.dto.BookingAutoCompleteResponse;
import com.guide.touristservice.dto.BookingSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class BookingApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiHost;
    private final String apiKey;

    public BookingApiClient(
            @Value("${booking.api.host}") String apiHost,
            @Value("${booking.api.key}") String apiKey,
            @Value("${booking.base.url}") String baseUrl
    ) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiHost = apiHost;
        this.apiKey = apiKey;
    }

    public BookingAutoCompleteResponse autoComplete(String city) {
        String url = baseUrl + "/attraction/auto-complete?query=" + city;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BookingAutoCompleteResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BookingAutoCompleteResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("autoComplete() API returned non-success status: {}", response.getStatusCode());
                throw new RuntimeException("API error: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("autoComplete() Unauthorized: {}", e.getResponseBodyAsString());
            throw new RuntimeException("API Unauthorized: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException e) {
            log.error("autoComplete() HTTP error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("API HTTP error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("autoComplete() exception: {}", e.getMessage());
            throw new RuntimeException("API exception: " + e.getMessage(), e);
        }
    }

    public BookingSearchResponse searchAttractions(String base64Id) {
        String url = baseUrl + "/attraction/search?id=" + base64Id;

        // Configure headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BookingSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BookingSearchResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("searchAttractions() API returned non-success status: {}", response.getStatusCode());
                throw new RuntimeException("API error: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("searchAttractions() Unauthorized: {}", e.getResponseBodyAsString());
            throw new RuntimeException("API Unauthorized: " + e.getResponseBodyAsString(), e);
        } catch (HttpClientErrorException e) {
            log.error("searchAttractions() HTTP error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("API HTTP error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("searchAttractions() exception: {}", e.getMessage());
            throw new RuntimeException("API exception: " + e.getMessage(), e);
        }
    }
}
