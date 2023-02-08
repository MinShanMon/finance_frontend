package com.team3.personalfinanceapp.network.api;

import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/api/transaction/{userId}")
    Call<List<Transaction>> getAllTransactions(@Path("userId") int userId, @Header("Authorization") String token);

    @GET("/api/transaction/{userId}?")
    Call<List<Transaction>> getTransactionsByMonth(@Path("userId") int userId, @Query("month") Integer month, @Header("Authorization") String token);

    @POST("/api/transaction/{userId}")
    Call<Transaction> addTransaction(@Path("userId") int userId, @Body Transaction transaction, @Header("Authorization") String token );
}
