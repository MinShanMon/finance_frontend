package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.team3.personalfinanceapp.model.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransactionActivity extends AppCompatActivity {

    private static final int TYPE_SPENDING = 0;
    private static final int TYPE_INCOME = 1;

    private int transactionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        setTransactionTypeDropdown();

        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(v -> saveTransaction());
    }

    private void setTransactionTypeDropdown() {
        Spinner spinner = findViewById(R.id.transaction_type_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText categoryField = findViewById(R.id.add_transaction_category);
                if (position == 0) {
                    transactionType = TYPE_SPENDING;
                    categoryField.setVisibility(View.VISIBLE);
                } else {
                    transactionType = TYPE_INCOME;
                    categoryField.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                EditText categoryField = findViewById(R.id.add_transaction_category);
                transactionType = TYPE_SPENDING;
                categoryField.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveTransaction() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Transaction newTransaction = new Transaction();

        EditText category = findViewById(R.id.add_transaction_category);


        EditText title = findViewById(R.id.add_transaction_title);
        newTransaction.setTitle(title.getText().toString());

        EditText description = findViewById(R.id.add_transaction_description);
        newTransaction.setDescription(description.getText().toString());

        EditText date = findViewById(R.id.add_transaction_date);
        newTransaction.setDate(LocalDate.parse(date.getText().toString(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        EditText amountField = findViewById(R.id.add_transaction_amount);

        double amount = Double.parseDouble(amountField.getText().toString());
        if (transactionType == TYPE_SPENDING) {
            amount *= -1;
        }
        if (transactionType == TYPE_INCOME) {
            category.setText("Income");
        }
        newTransaction.setAmount(amount);
        newTransaction.setCategory(category.getText().toString());

        Call<Transaction> addTransactionCall = apiInterface.addTransaction(1, newTransaction);


        System.out.println(addTransactionCall.request());
        addTransactionCall.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                call.cancel();
            }
        });

    }


}