package com.personalfinanceapp.frontend.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Email;

import org.springframework.stereotype.Service;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.personalfinanceapp.frontend.model.Token;
import com.personalfinanceapp.frontend.model.RegisteredUsers;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RegisteredUsersServiceImpl implements RegisteredUsersService {
    
    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    @Autowired
    WebClient webClient;

    public RegisteredUsersServiceImpl(@Value("${content-service}") String baseURL) {
        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .build();
    }
    // ---------------------- //

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            LOGGER.info("Request: {} {}" + clientRequest.method() + clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> LOGGER.info("{}={}" + name + value)));
            return next.exchange(clientRequest);
        };
    }

    @Override
    public Token login(String email, String password) {
        // TODO Auto-generated method stub
        Mono<Token> gettk = webClient.post()
                            .uri("/custom/login"+ "?email="+email+"&password="+password)
                            .retrieve()
                            .bodyToMono(Token.class);
        return gettk.block();
    }

    @Override
    public RegisteredUsers addAdminToSession(String email, String token) {
        // TODO Auto-generated method stub
        Mono<RegisteredUsers> getUser = webClient.get() // http request call
                .uri("/user/addSessionAdmin?email=" + email).header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(RegisteredUsers.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        return getUser.block();
    }

    @Override
    public Long checkToken(String email, String token){
        Mono<Long> checkToken = webClient.get()
                                .uri("/user/checkToken?email=" + email).header("Authorization", "Bearer "+token)
                                .accept(MediaType.APPLICATION_JSON)
                                .exchangeToMono(response -> {
                                    if (response.statusCode().equals(HttpStatus.OK)) {
                                        return response.bodyToMono(Long.class);
                                    } else {
                                        return response.createException().flatMap(Mono::error);
                                    }
                                });
                      

        return checkToken.block();
                                
    }

    @Override
    public Token refreshToken(String token){
        Mono<Token> getUser = webClient.get() // http request call
                .uri("/token/refresh").header("Authorization", "Bearer "+token)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(Token.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
        return getUser.block();
    }
}
