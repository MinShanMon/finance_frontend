package com.personalfinanceapp.frontend.Service;

import java.time.Duration;
import java.util.List;

import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.Models.FixedDeposits;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class FixedDepostisServiceImpl implements FixedDepostisService {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    @Autowired
    WebClient webClient;

    public FixedDepostisServiceImpl(@Value("${content-service}") String baseURL) {
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

    @Override
    public List<FixedDeposits> findAllFixeds(){

        Flux<FixedDeposits> fixedList = webClient.get()
                .uri("/fixeds")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(FixedDeposits.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return fixedList.collectList().block();

    }

    @Override
    public FixedDeposits addFixedDeposits(FixedDeposits fixedDeposits) {
        Mono<FixedDeposits> a_fixed = webClient.post()
                .uri("/addfixed")
                .body(Mono.just(fixedDeposits), FixedDeposits.class)
                .retrieve()
                .bodyToMono(FixedDeposits.class)
                .timeout(Duration.ofMillis(10_000));
        return a_fixed.block();
    }

}
