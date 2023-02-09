package com.personalfinanceapp.frontend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.model.Enquiry;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.logging.Logger;


@Service
public class EnquiryServiceImpl implements EnquiryService {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    @Autowired
    WebClient webClient;

    public EnquiryServiceImpl(@Value("${content-service-api}") String baseURL) {
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
    public List<Enquiry> viewDashboard(String token){
        Flux<Enquiry> enquiries = webClient.get()
                .uri("/admin/enquiries")
                .header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Enquiry.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return enquiries.collectList().block();
    }

    @Override
    public List<Enquiry> getAllEnquiry(String token){
        Flux<Enquiry> enquiries = webClient.get()
                .uri("/admin/enquiries")
                .header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Enquiry.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return enquiries.collectList().block();
    }

    @Override
    public List<Enquiry> getOpenEnquiry(String token){
        Flux<Enquiry> openEnquiries = webClient.get()
                .uri("/admin/enquiries/open")
                .header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Enquiry.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return openEnquiries.collectList().block();
    }

    @Override
    public List<Enquiry> getClosedEnquiry(String token){
        Flux<Enquiry> closedEnquiries = webClient.get()
                .uri("/admin/enquiries/closed")
                .header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToFlux(Enquiry.class);
                    } else {
                        return response.createException().flatMapMany(Flux::error);
                    }
                });

        return closedEnquiries.collectList().block();
    }

    @Override
    public Enquiry getOneEnquiry(Integer id, String token) {
        Mono<Enquiry> enq = webClient.get()
                .uri("/admin/view/{id}", id)
                .header("Authorization", "Bearer "+token)
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

}
