package com.team3.personalfinanceapp.utils;


import com.team3.personalfinanceapp.model.BankResponse;
import com.team3.personalfinanceapp.model.BankStatementResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface BankAPIInterface {

    @GET("summary*?accountType=D")
    Call<BankResponse> getAccountDetails(@Header("Authorization") String authHeader, @Query("accountNo") String accountNo);

    @GET("recentAccountActivity*?accountType=D")
    Call<BankStatementResponse> getStatementDetails(@Header("Authorization") String authHeader, @Query("accountNo") String accountNo);
}
