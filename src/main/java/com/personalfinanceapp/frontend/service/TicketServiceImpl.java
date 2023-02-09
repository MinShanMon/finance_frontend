package com.personalfinanceapp.frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import java.util.logging.Logger;

import com.personalfinanceapp.frontend.model.Ticket;

@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    @Autowired
    WebClient webClient;

    public TicketServiceImpl(@Value("${content-service-admin}") String baseURL) {
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

    // @Override
    // public Ticket create(Ticket tik) {
    //     Mono<Ticket> createdTik = webClient.post()
    //             .uri("/ticket")
    //             .body(Mono.just(tik), Ticket.class)
    //             .retrieve()
    //             .bodyToMono(Ticket.class)
    //             .timeout(Duration.ofMillis(10_000));
    //     return createdTik.block();
    // }

    @Override
    public Ticket update(Ticket tik){
        Mono<Ticket> updatedReply = webClient.put()
            .uri("/ticket")
            .body(Mono.just(tik), Ticket.class)
            .retrieve()
            .bodyToMono(Ticket.class);
        return updatedReply.block();
    }


    @Override
    public boolean sendEmail(Integer id){
    Mono<Boolean> sendemail = webClient.post()
            .uri("/sendmail/{id}",id)
            .retrieve()
            .bodyToMono(Boolean.class);
        return sendemail.block();
    }

    @Override
    public boolean sendReview(Integer id){
    Mono<Boolean> sendReview = webClient.post()
            .uri("/sendreview/{id}",id)
            .retrieve()
            .bodyToMono(Boolean.class);
        return sendReview.block();
    }
}    



 
   
