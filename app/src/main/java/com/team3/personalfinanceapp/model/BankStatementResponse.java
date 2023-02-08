package com.team3.personalfinanceapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankStatementResponse {

    private boolean success;
    private BankStatement results;

    @JsonCreator
    public BankStatementResponse(@JsonProperty("Success") boolean success, @JsonProperty("Results") BankStatement results) {
        this.success = success;
        this.results = results;
    }

    public BankStatement getResults() {
        return results;
    }

    /**
     * Nested class to map results object from BankStatement JSON
     **/

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BankStatement {

        private long accountNo;
        private List<ActivityDetails> activityDetails;

        BankStatement() {
        }

        @JsonCreator
        BankStatement(@JsonProperty("AccountNo") long accountNo, @JsonProperty("RecentAccountActivityDetails") List<ActivityDetails> activityDetails) {
            this.accountNo = accountNo;
            this.activityDetails = activityDetails;
        }


        public long getAccountNo() {
            return accountNo;
        }

        public List<ActivityDetails> getActivityDetails() {
            return activityDetails;
        }

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActivityDetails {
        private double averageBalance;

        private String date;

        ActivityDetails(){}


        @JsonCreator
        ActivityDetails(@JsonProperty("AverageBalance") double averageBalance, @JsonProperty("Date") String date) {
            this.averageBalance = averageBalance;
            this.date = date;
        }

        public double getAverageBalance() {
            return averageBalance;
        }

        @JsonIgnore
        public LocalDate getDate() {
            return LocalDate.parse(date);
        }
    }
}
