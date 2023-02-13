package com.personalfinanceapp.frontend.service;

import java.util.List;

import com.personalfinanceapp.frontend.Models.FixedDeposits;

public interface FixedDepostisService {

    List<FixedDeposits> findAllFixeds(String token);

    List<FixedDeposits> findfixedbybankid(Long id, String token);

    FixedDeposits addFixedDeposits(FixedDeposits fixedDeposits, String token);

    Long deletefixed(Long id, String token);

    FixedDeposits editfixed(FixedDeposits fixedDeposits, String token);

    FixedDeposits findbyid(Long id, String token);

}
