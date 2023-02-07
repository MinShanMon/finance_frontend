package com.team3.personalfinanceapp.utils;


import com.team3.personalfinanceapp.model.BankResponse;
import com.team3.personalfinanceapp.model.BankStatementResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface BankAPIInterface {

    @GET("summary*?accountNo=201770161001&accountType=D") //hardcoded account number
    Call<BankResponse> getAccountDetails(@Header("Authorization") String authHeader);

    @GET("recentAccountActivity*?accountNo=201770161001&accountType=D")
    Call<BankStatementResponse> getStatementDetails(@Header("Authorization") String authHeader);
}
