package com.team3.personalfinanceapp.network.api;

import com.google.gson.JsonObject;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.model.Status;
import com.team3.personalfinanceapp.model.Token;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {
    @FormUrlEncoded
    @POST("/api/custom/login")
    Call<Token> userLogin(@Field("email") String email, @Field("password") String password);

    @GET("/api/user/addSessionUser")
    Call<RegisteredUsers> getUserInfo(@Query("email") String email, @Header("Authorization") String token);

    @POST("/api/user/register")
    Call<RegisteredUsers> regUser(@Body RegisteredUsers user);

    @GET("/api/user/sentOTPByEmail")
    Call<Token> sendOTPByEmail(@Query("email") String email);

    @GET("/api/user/otpverify")
    Call<Token> verifyOTP(@Query("email") String email, @Query("otp") String otp, @Header("Authorization")String token);

    @POST("/api/user/resetpassword")
    Call<Object> resetPassword(@Query("email") String email, @Query("password") String password, @Query("otp") String otp, @Header("Authorization")String token);

    @POST("/api/user/loginWithFb")
    Call<RegisteredUsers> regFbUser(@Body RegisteredUsers user);

    @GET("/api/user/checkTokenAndroid")
    Call<Object> checkToken(@Query("id") Integer id,  @Header("Authorization")String token);



}
