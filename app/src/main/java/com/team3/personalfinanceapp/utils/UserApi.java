package com.team3.personalfinanceapp.utils;

import com.google.gson.JsonObject;
import com.team3.personalfinanceapp.model.Enquiry;
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
    @POST("custom/login")
    Call<Token> userLogin(@Field("email") String email, @Field("password") String password);

    @GET("user/addSessionUser")
    Call<RegisteredUsers> getUserInfo(@Query("email") String email, @Header("Authorization") String token);

    @POST("user/register")
    Call<RegisteredUsers> regUser(@Body RegisteredUsers user);

    @GET("user/sentOTPByEmail")
    Call<Token> sendOTPByEmail(@Query("email") String email);

    @GET("user/otpverify")
    Call<Token> verifyOTP(@Query("email") String email, @Query("otp") String otp, @Header("Authorization")String token);

    @POST("user/resetpassword")
    Call<Object> resetPassword(@Query("email") String email, @Query("password") String password, @Query("otp") String otp, @Header("Authorization")String token);

    @POST("user/loginWithFb")
    Call<RegisteredUsers> regFbUser(@Body RegisteredUsers user);

    @GET("user/checkToken")
    Call<Token> checkToken(@Query("id") Integer id,  @Header("Authorization")String token);

    @POST("user/edit/password")
    Call<Object> editPassword(@Query("id") Integer id, @Query("password") String password, @Header("Authorization")String token);

    @POST("user/edit/profile")
    Call<RegisteredUsers> editProfile(@Body RegisteredUsers user, @Header("Authorization")String token);

    @POST("user/enquiry")
    Call<Enquiry> createEnquiry(@Body Enquiry enquiryObj);
}
