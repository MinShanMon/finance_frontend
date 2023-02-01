package com.personalfinanceapp.frontend.Service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.FixedDeposits;

public interface FixedDepostisService {

    List<FixedDeposits> findAllFixeds();

    FixedDeposits addFixedDeposits(FixedDeposits fixedDeposits);
}
