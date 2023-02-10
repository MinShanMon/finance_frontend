package com.personalfinanceapp.frontend.config;

import reactor.netty.http.client.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient getWebClient(WebClient.Builder webClientBuilder){

       return webClientBuilder
               .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
               .baseUrl("http://adteam3restapi-env.eba-p5rw8sy5.ap-northeast-1.elasticbeanstalk.com/api")
               .build();
    }

    private HttpClient getHttpClient(){
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10))
                .addHandlerLast(new WriteTimeoutHandler(10)));
    }
}