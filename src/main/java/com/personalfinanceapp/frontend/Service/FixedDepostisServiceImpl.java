package com.personalfinanceapp.frontend.Service;

import java.time.Duration;
import java.util.List;

import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
    public FixedDeposits editfixed(FixedDeposits fixedDeposits) {
        Mono<FixedDeposits> _editfixed = webClient.put()
                .uri("/editfixed/")
                .body(Mono.just(fixedDeposits), FixedDeposits.class)
                .retrieve()
                .bodyToMono(FixedDeposits.class);
        return _editfixed.block();
    }

    @Override
    public FixedDeposits findbyid(Long id) {
        Mono<FixedDeposits> bank = webClient.get()
                .uri("/fixed/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(FixedDeposits.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        return bank.block();
    }



    @Override
    public List<FixedDeposits> findfixedbybankid(Long id){

        Flux<FixedDeposits> fixedList = webClient.get()
                .uri("/fixeds/"+ id)
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

    @Override
    public Long deletefixed(Long id){
        Mono<Long> _deletefixed = webClient.delete()
        .uri("/deletefixed/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> {
            return Mono.error(NotFoundException::new);
        })
        .onStatus(HttpStatus::is5xxServerError, response -> {
            return Mono.error(UnknownError::new);
        })
        .bodyToMono(Long.class)
        .onErrorComplete();
        return _deletefixed.block();
    }
}
