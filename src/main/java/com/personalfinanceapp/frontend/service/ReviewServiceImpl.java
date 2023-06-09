package com.personalfinanceapp.frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.model.Enquiry;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.logging.Logger;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    @Autowired
    WebClient webClient;

    public ReviewServiceImpl(@Value("${content-service-admin}") String baseURL) {
        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            LOGGER.info("Request: {} {}" + clientRequest.method() + clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> LOGGER.info("{}={}" + name + value)));
            return next.exchange(clientRequest);
        };
    }


    //open source
    @Override
    public Enquiry getOneReview(Integer id) {
        Mono<Enquiry> enq = webClient.get()
            .uri("/customer/review/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(Enquiry.class);
                } else {
                    return response.createException().flatMap(Mono::error);
                }
            });
    return enq.block();
    }

    //open source
    @Override
    public Enquiry updateReview(Enquiry enq){
        Mono<Enquiry> updatedReview = webClient.put()
            .uri("/customer/rate")
            .body(Mono.just(enq), Enquiry.class)
            .retrieve()
            .bodyToMono(Enquiry.class);
        return updatedReview.block();
    }

}

