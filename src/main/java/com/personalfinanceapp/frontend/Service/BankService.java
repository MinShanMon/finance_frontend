package com.personalfinanceapp.frontend.Service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.Bank;

public interface BankService {
    
    List<Bank> findAllBank();

    Bank addBank(Bank bank);

    Bank findBankById(Long id);


}
