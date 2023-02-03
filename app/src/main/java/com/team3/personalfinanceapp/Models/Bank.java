package com.team3.personalfinanceapp.Models;

public class Bank {

    private long b_id;
    private String bankName;
    private String bankLink;

    public long getB_id() {
        return b_id;
    }

    public void setB_id(long b_id) {
        this.b_id = b_id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankLink() {
        return bankLink;
    }

    public void setBankLink(String bankLink) {
        this.bankLink = bankLink;
    }

    public Bank(long b_id, String bankName, String bankLink) {
        this.b_id = b_id;
        this.bankName = bankName;
        this.bankLink = bankLink;
    }

    public Bank() {

    }
}
