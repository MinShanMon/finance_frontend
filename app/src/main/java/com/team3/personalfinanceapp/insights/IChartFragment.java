package com.team3.personalfinanceapp.insights;

import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

public interface IChartFragment {

    void updateTransactions(List<Transaction> transactions);
}
