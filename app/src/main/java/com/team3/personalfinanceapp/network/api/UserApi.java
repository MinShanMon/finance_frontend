package com.team3.personalfinanceapp.network.api;

import com.team3.personalfinanceapp.model.Token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {
    @FormUrlEncoded
    @POST("/api/custom/login")
    Call<Token> userLogin(@Field("email") String email, @Field("password") String password);
}
