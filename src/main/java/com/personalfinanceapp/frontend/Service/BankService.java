package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.Bank;

public interface BankService {
    
    List<Bank> findAllBank(String token);

    Bank addBank(Bank bank, String token);

    Bank findBankById(Long id, String token);

    Long deletebank(Long id, String token);

    Bank editbank(Bank bank, String token);


}
