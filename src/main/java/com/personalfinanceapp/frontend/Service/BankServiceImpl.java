package com.personalfinanceapp.frontend.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.Models.Bank;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;
import org.springframework.stereotype.Service;


@Service
public class BankServiceImpl implements BankService {


    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
    @Autowired
    WebClient webClient;

    public BankServiceImpl(@Value("${content-service}") String baseURL) {
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
    public List<Bank> findAllBank(){

        Flux<Bank> bankList = webClient.get()
                .uri("/banks")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Bank.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return bankList.collectList().block();

    }

    @Override
    public Bank addBank(Bank bank) {
        Mono<Bank> a_bank = webClient.post()
                .uri("/addbank")
                .body(Mono.just(bank), Bank.class)
                .retrieve()
                .bodyToMono(Bank.class)
                .timeout(Duration.ofMillis(10_000));
        return a_bank.block();
    }
    

    @Override
    public Bank findBankById(Long id) {
        Mono<Bank> bank = webClient.get()
                .uri("/bank/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(Bank.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        return bank.block();
    }

    @Override
    public Bank editbank(Bank bank) {
        Mono<Bank> _editbank = webClient.put()
                .uri("/editbank/")
                .body(Mono.just(bank), Bank.class)
                .retrieve()
                .bodyToMono(Bank.class);
        return _editbank.block();
    }

    @Override
    public Long deletebank(Long id){
        Mono<Long> _deletebank = webClient.delete()
        .uri("/deletebank/" + id)
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
        return _deletebank.block();
    }

}

