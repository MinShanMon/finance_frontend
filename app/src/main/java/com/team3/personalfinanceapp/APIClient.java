package com.team3.personalfinanceapp;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class APIClient {
    static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:80/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
