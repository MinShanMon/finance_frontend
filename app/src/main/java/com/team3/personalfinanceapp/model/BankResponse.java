package com.team3.personalfinanceapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankResponse {

    public BankResponse() {
    }


    @JsonCreator
    public BankResponse(@JsonProperty("Success") boolean success, @JsonProperty("Results") BankAccount results) {
        this.success = success;
        this.results = results;
    }

    private boolean success;
    private BankAccount results;

    public Boolean isSuccess() {
        return success;
    }

    public BankAccount getResults() {
        return results;
    }

    @JsonIgnore
    public double getAvailableBalance() {
        return getResults().getStaticBalanceDetail().getAvailableBalance();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BankAccount {

        private long accountNo;
        private StaticBalanceDetail staticBalanceDetail;

        BankAccount() {
        }

        @JsonCreator
        BankAccount(@JsonProperty("AccountNo") long accountNo, @JsonProperty("StaticBalanceDetail") StaticBalanceDetail staticBalanceDetail) {
            this.accountNo = accountNo;
            this.staticBalanceDetail = staticBalanceDetail;
        }


        public long getAccountNo() {
            return accountNo;
        }

        public StaticBalanceDetail getStaticBalanceDetail() {
            return staticBalanceDetail;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class StaticBalanceDetail {

            private double availableBalance;

            public StaticBalanceDetail() {}

            @JsonCreator
            public StaticBalanceDetail(@JsonProperty("AvailableBalance") double availableBalance) {
                this.availableBalance = availableBalance;
            }

            public double getAvailableBalance() {
                return availableBalance;
            }
        }

    }

}
