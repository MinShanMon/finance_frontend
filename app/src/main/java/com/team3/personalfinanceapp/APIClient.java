package com.team3.personalfinanceapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class APIClient {
    static final String BASE_URL = "http://10.0.2.2:80/api/";
    static final String BANK_URL = "https://api.ocbc.com:8243/transactional/account/1.0/";

    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logger)
            .build();

    static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    static Retrofit getBankClient() {
        return new Retrofit.Builder()
                .baseUrl(BANK_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
