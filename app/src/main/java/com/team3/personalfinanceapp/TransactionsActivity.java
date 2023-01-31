package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.team3.personalfinanceapp.adapter.TransactionAdapter;
import com.team3.personalfinanceapp.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Intent intent = getIntent();
        transactions = (ArrayList<Transaction>) intent.getSerializableExtra("transactions");


        ListView transactionsListView = findViewById(R.id.transactions_listview);
        transactionsListView.setAdapter(new TransactionAdapter(this, transactions));
    }

}