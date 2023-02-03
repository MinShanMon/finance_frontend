package com.team3.personalfinanceapp.Models;

import com.google.gson.annotations.SerializedName;
import com.team3.personalfinanceapp.Models.Bank;

import java.time.LocalDateTime;

public class FixedDeposits {

    private long f_id;
    private int tenure;
    private int minAmount;
    private int maxAmount;
    private double interestRate;
    private String updateDate;

    @SerializedName("fd_bank")
    private Bank bank;

    public long getId() {
        return f_id;
    }

    public void setId(long f_id) {
        this.f_id = f_id;
    }

    public int getTenure() {
        return tenure;
    }

    public void setTenure(int tenure) {
        this.tenure = tenure;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public FixedDeposits(long f_id, int tenure, int minAmount, int maxAmount, double interestRate, String updateDate, Bank bank) {
        this.f_id = f_id;
        this.tenure = tenure;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.updateDate = updateDate;
        this.bank = bank;
    }

    public FixedDeposits(int tenure, int minAmount, int maxAmount, double interestRate, String updateDate, Bank bank) {
        this.tenure = tenure;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.updateDate = updateDate;
        this.bank = bank;
    }

    public FixedDeposits() {

    }
}
