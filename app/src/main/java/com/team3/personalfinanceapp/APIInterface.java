package com.team3.personalfinanceapp;

import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {
    @GET("transaction/{userId}")
    Call<List<Transaction>> transList(@Path("userId") long userId);
}
