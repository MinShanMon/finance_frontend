package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.team3.personalfinanceapp.model.Transaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTransactionActivity extends AppCompatActivity {

    private static final int TYPE_SPENDING = 0;
    private static final int TYPE_INCOME = 1;
    private Transaction transactionToEdit;
    private int transactionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        Intent intent = getIntent();
        long transactionId = intent.getLongExtra("transactionId", 0);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<Transaction> getTransactionCall = apiInterface.getTransactionById(transactionId);
        getTransactionCall.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                transactionToEdit = response.body();
                updateTransactionForm(transactionToEdit);
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void updateTransactionForm(Transaction transaction) {

        EditText category = findViewById(R.id.edit_transaction_category);
        category.setText(transaction.getCategory());
        if (transaction.getAmount() < 0) {
            transactionType = TYPE_SPENDING;
        } else {
            transactionType = TYPE_INCOME;
        }
        setTransactionTypeDropdown();
    }

    private void setTransactionTypeDropdown() {
        Spinner spinner = findViewById(R.id.edit_transaction_type_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(transactionType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText categoryField = findViewById(R.id.edit_transaction_category);
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
}