package com.team3.personalfinanceapp;

import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("transaction/{userId}")
    Call<List<Transaction>> getAllTransactions(@Path("userId") long userId);

    @GET("transaction/{userId}?")
    Call<List<Transaction>> getTransactionsByMonth(@Path("userId") long userId, @Query("month") Integer month);
}
