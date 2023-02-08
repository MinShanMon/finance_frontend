package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import com.team3.personalfinanceapp.adapter.TransactionAdapter;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.network.api.APIInterface;
import com.team3.personalfinanceapp.util.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    ArrayList<Transaction> transactions;
    APIInterface apiInterface;

    SharedPreferences pref;

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
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(pref.getInt("userid",0),"Bearer "+pref.getString("token", ""));
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