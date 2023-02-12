package com.team3.personalfinanceapp.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Enquiry implements Serializable{

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("enquiryType")
    @Expose
    private String enquiryType;

    @SerializedName("fullName")
    @Expose
    private String fullName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("question")
    @Expose
    private String question;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime enquiry_dateTime;

//    @SerializedName("rating")
//    @Expose
//    private int rating;
//
//    @SerializedName("comment")
//    @Expose
//    private String comment;

    public Enquiry() {
        // default constructor for Jackson
    }

    public int getId() {
        return id;
    }

    public String getEnquiryType() {
        return enquiryType;
    }
    public void setEnquiryType(String enquiryType) {
        this.enquiryType = enquiryType;
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
        this.email= email;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getEnquiry_dateTime() {
        return enquiry_dateTime;
    }
    public void setEnquiry_dateTime(LocalDateTime enquiry_dateTime) {
        this.enquiry_dateTime =enquiry_dateTime;
    }



//    public void setComment(String comment) {
//            this.comment = comment;
//    }

}
