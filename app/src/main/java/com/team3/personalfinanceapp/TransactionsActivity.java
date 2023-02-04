package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.team3.personalfinanceapp.adapter.TransactionAdapter;
import com.team3.personalfinanceapp.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    ArrayList<Transaction> transactions;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        getAllTransactionsAndDisplay();
    }


    /**
     * Retrieve all transactions and display all transactions
     **/
    private void getAllTransactionsAndDisplay() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(1);
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.body() == null) {
                    call.cancel();
                }
                transactions = new ArrayList<>(response.body());
                ListView transactionsListView = findViewById(R.id.transactions_listview);
                transactionsListView.setAdapter(new TransactionAdapter(TransactionsActivity.this, transactions));
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

}