package com.team3.personalfinanceapp.model;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RegisteredUsers {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fbid")
    @Expose
    private Object fbid;
    @SerializedName("otp")
    @Expose
    private Object otp;
    @SerializedName("otpReqTime")
    @Expose
    private Object otpReqTime;
    @SerializedName("jwtToken")
    @Expose
    private String jwtToken;
    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions;
    @SerializedName("roleSet")
    @Expose
    private List<Role> roleSet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getFbid() {
        return fbid;
    }

    public void setFbid(Object fbid) {
        this.fbid = fbid;
    }

    public Object getOtp() {
        return otp;
    }

    public void setOtp(Object otp) {
        this.otp = otp;
    }

    public Object getOtpReqTime() {
        return otpReqTime;
    }

    public void setOtpReqTime(Object otpReqTime) {
        this.otpReqTime = otpReqTime;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(List<Role> roleSet) {
        this.roleSet = roleSet;
    }

}