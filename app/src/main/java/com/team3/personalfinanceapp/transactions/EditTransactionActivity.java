package com.team3.personalfinanceapp.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int TYPE_SPENDING = 0;
    private static final int TYPE_INCOME = 1;
    private Transaction transactionToEdit;
    private int transactionType;
    private long transactionId;

    private SharedPreferences pref;

    private Button setDatePicker;

    private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        setDatePicker = findViewById(R.id.set_date_picker);

        ImageView backBtn = findViewById(R.id.img_backArrow);
        backBtn.setOnClickListener( e -> finish());

        Intent intent = getIntent();
        transactionId = intent.getLongExtra("transactionId", 0);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<Transaction> getTransactionCall = apiInterface.getTransactionById(transactionId, "Bearer "+ pref.getString("token" , ""));
        getTransactionCall.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                transactionToEdit = response.body();
                updateTransactionForm(transactionToEdit);
                setDatePicker.setOnClickListener( e -> {
                    DatePickerFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                });
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void updateTransactionForm(Transaction transaction) {


        if (transaction.getCategory().equalsIgnoreCase("income")) {
            transactionType = TYPE_INCOME;
        } else {
            transactionType = TYPE_SPENDING;
        }
        setTransactionTypeDropdown();

        EditText category = findViewById(R.id.edit_transaction_category);
        category.setText(transaction.getCategory());

        EditText title = findViewById(R.id.edit_transaction_title);
        title.setText(transaction.getTitle());

        EditText description = findViewById(R.id.edit_transaction_description);
        description.setText(transaction.getDescription());

        setDatePicker.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyy")));

        EditText amount = findViewById(R.id.edit_transaction_amount);
        amount.setText(Double.toString(Math.abs(transaction.getAmount())));

        setSaveButton();
        setDeleteButton();
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
                EditText categoryField = findViewById(R.id.edit_transaction_category);
                transactionType = TYPE_SPENDING;
                categoryField.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setSaveButton() {
        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener( v -> saveTransaction());
    }

    private void setDeleteButton() {
        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener( v -> deleteTransaction());
    }

    private void saveTransaction() {

        EditText title = findViewById(R.id.edit_transaction_title);
        transactionToEdit.setTitle(title.getText().toString());

        EditText description = findViewById(R.id.edit_transaction_description);
        transactionToEdit.setDescription(description.getText().toString());

        transactionToEdit.setDate(LocalDate.parse(setDatePicker.getText().toString(),
                DateTimeFormatter.ofPattern("dd MMM yy")));

        EditText amountField = findViewById(R.id.edit_transaction_amount);

        double amount = Double.parseDouble(amountField.getText().toString());
        EditText category = findViewById(R.id.edit_transaction_category);

        if (transactionType == TYPE_INCOME) {
            category.setText("Income");
        }
        transactionToEdit.setAmount(amount);
        transactionToEdit.setCategory(category.getText().toString());
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<Transaction> editTransactionCall = apiInterface.editTransaction(pref.getInt("userid", 0), transactionToEdit, "Bearer "+ pref.getString("token" , ""));

        editTransactionCall.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                errorToast();
                call.cancel();
            }
        });
    }

    private void deleteTransaction() {
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<Long> deleteCall = apiInterface.deleteTransactionById(transactionId, "Bearer "+ pref.getString("token" , ""));
        deleteCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    finish();
                } else if (response.code() == 404) {
                    Toast.makeText(EditTransactionActivity.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                errorToast();
                call.cancel();
            }
        });
    }

    private void errorToast() {
        Toast.makeText(EditTransactionActivity.this, "Network error. Please try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
        String dateString = DateTimeFormatter.ofPattern("dd MMM yy").format(date);
        setDatePicker.setText(dateString);
    }
}