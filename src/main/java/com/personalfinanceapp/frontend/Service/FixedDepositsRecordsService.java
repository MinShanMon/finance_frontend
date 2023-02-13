package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.Bank;
import com.personalfinanceapp.frontend.Models.FixedDepositsRecords;

public interface FixedDepositsRecordsService {

    FixedDepositsRecords recordFixed(FixedDepositsRecords fixedDeposisRecords, String token);
}
