package com.team3.personalfinanceapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BankStatementResponse {

    private boolean success;
    private ActivityDetails results;

    @JsonCreator
    public BankStatementResponse(@JsonProperty("Success") boolean success, @JsonProperty("Results") ActivityDetails results) {
        this.success = success;
        this.results = results;
    }

    /** Nested class to map results object from BankStatement JSON **/

    public static class BankStatement {

        private long accountNo;
        private ActivityDetails activityDetails;

        BankStatement(){}

        @JsonCreator
        BankStatement(@JsonProperty("AccountNo") long accountNo, @JsonProperty("RecentAccountActivityDetails") ActivityDetails activityDetails) {
            this.accountNo = accountNo;
            this.activityDetails = activityDetails;
        }

        public double getBalanceDetail() {
            return getActivityDetails().getAverageBalance();
        }

        public long getAccountNo() {
            return accountNo;
        }

        public ActivityDetails getActivityDetails() {
            return activityDetails;
        }

        public double getAverageBalance() {
        return getActivityDetails().getAverageBalance();
        }
    }

        public static class ActivityDetails {
            private double averageBalance;

            @JsonCreator
            ActivityDetails(@JsonProperty("AverageBalance") double averageBalance) {
                this.averageBalance = averageBalance;
            }

            public double getAverageBalance() {
                return averageBalance;
            }
        }
}
