package com.team3.personalfinanceapp.config;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIclient {
    private Retrofit retrofit;

    public APIclient(){
        initializeRetrofit();
    }


    private void initializeRetrofit(){
        retrofit= new Retrofit.Builder()
                .baseUrl("http://adteam3restapi-env.eba-p5rw8sy5.ap-northeast-1.elasticbeanstalk.com/api/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }
    public Retrofit getRetrofit(){
        return retrofit;
    }


}



