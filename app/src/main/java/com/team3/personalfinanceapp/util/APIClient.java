package com.team3.personalfinanceapp.util;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class APIClient {
    static final String BASE_URL = "http://192.168.1.126:8081";
//    static final String BASE_URL = "http://10.249.228.3:8081";
    static final String BANK_URL = "https://api.ocbc.com:8243/transactional/account/1.0/";

    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logger)
            .build();

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static Retrofit getBankClient() {
        return new Retrofit.Builder()
                .baseUrl(BANK_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
