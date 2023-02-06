package com.team3.personalfinanceapp;

import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("transaction/{userId}")
    Call<List<Transaction>> getAllTransactions(@Path("userId") int userId);

    @GET("transaction/{userId}?")
    Call<List<Transaction>> getTransactionsByMonth(@Path("userId") int userId, @Query("month") Integer month);

    @GET("transaction")
    Call<Transaction> getTransactionById(@Query("transactionId") long transactionId);

    @POST("transaction/{userId}")
    Call<Transaction> addTransaction(@Path("userId") int userId, @Body Transaction transaction);

    @PUT("transaction")
    Call<Transaction> editTransaction(@Path("userId") int userId, @Body Transaction transaction);


}
