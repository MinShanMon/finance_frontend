package com.personalfinanceapp.frontend.Models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "banks")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long b_id;

    private String bankName;

    private String bankLink;

    @OneToMany(mappedBy = "fd_bank", cascade = CascadeType.ALL)
    private List<FixedDeposits> b_fixedDeposits;


    public Bank(String bankName, String bankLink){
        this.bankName = bankName;
        this.bankLink = bankLink;
    }


}
