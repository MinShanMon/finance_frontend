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
import java.time.Duration;

import com.personalfinanceapp.frontend.model.Ticket;


@Service
public class TicketServiceImpl implements TicketService {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    @Autowired
    WebClient webClient;

    public TicketServiceImpl(@Value("${content-service}") String baseURL) {
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
    public Ticket create(Ticket tik) {
        Mono<Ticket> createdTik = webClient.post()
                .uri("/ticket")
                .body(Mono.just(tik), Ticket.class)
                .retrieve()
                .bodyToMono(Ticket.class)
                .timeout(Duration.ofMillis(10_000));
        return createdTik.block();
    }

    @Override
    public Ticket update(Ticket tik){
        Mono<Ticket> updatedReply = webClient.put()
            .uri("/ticket")
            .body(Mono.just(tik), Ticket.class)
            .retrieve()
            .bodyToMono(Ticket.class);
        return updatedReply.block();
    }


    // @PostMapping("/reply/{id}")
    // public ResponseEntity<?> replyEmail(@PathVariable int id) throws MessagingException, UnsupportedEncodingException{
    // {
    //     boolean result = this.tikService.sendEmail(id);

    //     if(result){

    //         return  ResponseEntity.ok("Email Sent Successfully.");

    //     }else{

    //         return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email Sending Fails");
    //     }
    // }
}   
