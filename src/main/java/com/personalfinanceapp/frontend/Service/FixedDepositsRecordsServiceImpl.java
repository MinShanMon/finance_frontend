package com.personalfinanceapp.frontend.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.Models.Bank;
import com.personalfinanceapp.frontend.Models.FixedDepositsRecords;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;
import org.springframework.stereotype.Service;


@Service
public class FixedDepositsRecordsServiceImpl implements FixedDepositsRecordsService {


    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    @Autowired
    WebClient webClient;

    public FixedDepositsRecordsServiceImpl(@Value("${content-service}") String baseURL) {
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
    public FixedDepositsRecords recordFixed(FixedDepositsRecords fixedDeposisRecords){
        Mono<FixedDepositsRecords> recordF = webClient.post()
            .uri("/recordfixed")
            .body(Mono.just(fixedDeposisRecords), FixedDepositsRecords.class)
            .retrieve()
            .bodyToMono(FixedDepositsRecords.class)
            .timeout(Duration.ofMillis(10_000));
        return recordF.block();
    }
    
}

