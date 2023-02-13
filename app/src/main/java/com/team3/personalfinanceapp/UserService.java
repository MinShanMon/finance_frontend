package com.team3.personalfinanceapp;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("users/")
    Call<UserResponse> saveUsers(@Body UserRequest userRequest);

}