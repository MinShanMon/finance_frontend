package com.team3.personalfinanceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    private long id;

    private String title;
    private String description;

    private String date;

    private String category;
    private double amount;

    private String user;


    public Transaction(long id, String title, String description, String date, String category, double amount, String user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.user = user;
    }

    public Transaction() {}

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
