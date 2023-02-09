package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.FixedDeposits;

public interface FixedDepostisService {

    List<FixedDeposits> findAllFixeds();

    List<FixedDeposits> findfixedbybankid(Long id);

    FixedDeposits addFixedDeposits(FixedDeposits fixedDeposits);

    Long deletefixed(Long id);

    FixedDeposits editfixed(FixedDeposits fixedDeposits);

    FixedDeposits findbyid(Long id);

}
