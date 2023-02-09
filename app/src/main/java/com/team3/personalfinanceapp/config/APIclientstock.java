package com.team3.personalfinanceapp.config;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIclientstock {
    private Retrofit retrofit;

    public APIclientstock(){
        initializeRetrofit();
    }


    static HttpLoggingInterceptor logger = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS);


    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logger).build();

    private void initializeRetrofit(){
        retrofit= new Retrofit.Builder()
                .baseUrl("https://api.twelvedata.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }
    public Retrofit getRetrofit(){
        return retrofit;
    }


}



