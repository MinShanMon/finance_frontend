package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.Bank;

public interface BankService {
    
    List<Bank> findAllBank();

    Bank addBank(Bank bank);

    Bank findBankById(Long id);

    Long deletebank(Long id);

    Bank editbank(Bank bank);


}