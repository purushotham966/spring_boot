package com.moveinsync.bem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class VerveService {

    private final VerveCaching caching;
    private final WebClient webClient;

    public VerveService(VerveCaching caching, WebClient webClient) {
        this.caching = caching;
        this.webClient = webClient;
    }


    public void registerRequestIds(Integer id) {
        caching.registerRequest(id);
    }


    public void httpRequest(String endpoint, String path, String methodType, Object request) {
        WebClient.RequestHeadersSpec<?> requestSpec;

        if ("POST".equalsIgnoreCase(methodType)) {

            requestSpec = webClient.post()
                    .uri(endpoint + path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .accept(MediaType.APPLICATION_JSON);
        } else {

            requestSpec = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(path)
                            .queryParam("count", request)
                            .build())
                    .accept(MediaType.APPLICATION_JSON);
        }

        requestSpec.retrieve()
                .toBodilessEntity() // No response body needed
                .doOnSuccess(response -> log.info("{} request successful. URL: {}{}, Status Code: {}", methodType, endpoint, path, response.getStatusCode()))
                .doOnError(error -> log.error("{} request failed: {}", methodType, error.getMessage()))
                .subscribe(); // Asynchronous execution, avoids blocking
    }

}
