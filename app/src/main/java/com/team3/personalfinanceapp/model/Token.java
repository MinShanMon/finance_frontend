package com.team3.personalfinanceapp.model;


import com.google.gson.annotations.SerializedName;


public class Token {

    @SerializedName("access_token")
    public String access_token;
    @SerializedName("refresh_token")
    public String refresh_token;
    @SerializedName("status")
    public String status;
}
