package com.personalfinanceapp.frontend.Models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "fixed_deposits_recode")
public class FixedDepositsRecords {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fr_id;

    private int tenure;

    private int minAmount;

    private int maxAmount;

    private double interestRate;

    private int month;

    private int year;

    private String bankName;
    


    public FixedDepositsRecords(int tenure, int minAmount, int maxAmount, double interestRate, int month, int year, String bankName){
         

        this.tenure = tenure;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.month = month;
        this.year = year;
        this.bankName = bankName;

    }
}
