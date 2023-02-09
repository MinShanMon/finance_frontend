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
@Table(name = "fixed_deposits")
public class FixedDeposits {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long f_id;

    private int tenure;

    private int minAmount;

    private int maxAmount;

    private double interestRate;

    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank fd_bank;
    


    public FixedDeposits(int tenure, int minAmount, int maxAmount, double interestRate, LocalDateTime updateDate, Bank fd_bank){
         

        this.tenure = tenure;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.interestRate = interestRate;
        this.updateDate = updateDate;
        this.fd_bank = fd_bank;

    }
}
