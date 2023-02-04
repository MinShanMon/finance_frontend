package com.team3.personalfinanceapp;


import com.team3.personalfinanceapp.model.BankResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface BankAPIInterface {
    @GET("summary*?accountNo=12345678&accountType=D") //hardcoded account number
    Call<BankResponse> getAccountDetails(@Header("Authorization") String authHeader);
}
