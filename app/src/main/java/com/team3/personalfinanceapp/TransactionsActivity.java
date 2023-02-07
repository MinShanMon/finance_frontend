package com.team3.personalfinanceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team3.personalfinanceapp.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    APIInterface apiInterface;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionslist);
        linearLayout = findViewById(R.id.transactions_list_linearlayout);
        getAllTransactionsAndDisplay();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        linearLayout.removeAllViews();
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
                List<Transaction> transactions = new ArrayList<>(response.body());
                Map<LocalDate, List<Transaction>> transactionsByDate = groupTransactionsByDate(transactions);
                displayTransactions(transactionsByDate);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private SortedMap<LocalDate, List<Transaction>> groupTransactionsByDate(List<Transaction> transactions) {
        return transactions.stream().collect(
                Collectors.groupingBy(
                        Transaction::getDate,
                        (Supplier<SortedMap<LocalDate, List<Transaction>>>) () ->
                                new TreeMap<>(Comparator.reverseOrder()),
                        Collectors.toList()
                )
        );
    }

    private void displayTransactions(Map<LocalDate, List<Transaction>> transactionsByDate) {

        transactionsByDate.forEach( (date, ts) -> {
            TextView dateText = new TextView(this);
            dateText.setText(date.toString());
            dateText.setTypeface(null, Typeface.BOLD);
            dateText.setBackgroundColor(Color.parseColor("#DCDCDC"));
            linearLayout.addView(dateText);

            ts.forEach( t -> {
                TextView transactionText = new TextView(this);
                String transStr = t.getTitle() + "\n"
                        + t.getCategory() + "\n"
                        + t.getAmount();
                transactionText.setText(transStr);
                transactionText.setOnClickListener( v -> {
                    Intent intent = new Intent(this, EditTransactionActivity.class);
                    intent.putExtra("transactionId", t.getId());
                    startActivity(intent);
                });

                linearLayout.addView(transactionText);

                View viewDivider = new View(this);
                viewDivider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                viewDivider.setBackgroundColor(Color.parseColor("#000000"));
                linearLayout.addView(viewDivider);

            });

        });

    }

}