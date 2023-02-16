package com.team3.personalfinanceapp.transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.transactions.EditTransactionActivity;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private LinearLayout linearLayout;

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
        TextView title = findViewById(R.id.transaction_list_title);
        title.setText(getString(R.string.all_transactions_title));
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(pref.getInt("userid", 0), "Bearer " + pref.getString("token", ""));
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

        transactionsByDate.forEach((date, ts) -> {
            TextView dateText = new TextView(this);
            String dateString = DateTimeFormatter.ofPattern("dd MMM yyyy").format(date);
            dateText.setText(dateString);
            dateText.setTypeface(null, Typeface.BOLD);
            dateText.setBackgroundColor(Color.parseColor("#DCDCDC"));
            dateText.setPadding(50,20,50,30);
            linearLayout.addView(dateText);

            ts.forEach(t -> {
                LinearLayout linearLayoutTransaction = new LinearLayout(this);
                linearLayoutTransaction.setOrientation(LinearLayout.VERTICAL);
                linearLayoutTransaction.setPadding(50, 20,50,30);

                TextView transactionTitleText = new TextView(this);
                transactionTitleText.setText(t.getTitle());

                TextView transactionCategoryText = new TextView(this);
                transactionCategoryText.setText(t.getCategory());



                TextView transactionAmtText = new TextView(this);
                transactionAmtText.setText("$" + String.format(getString(R.string.money_format), t.getAmount()));
                transactionAmtText.setTypeface(null, Typeface.BOLD);
                transactionAmtText.setTextSize(20);
                transactionAmtText.setGravity(Gravity.END);

                linearLayoutTransaction.addView(transactionTitleText);
                linearLayoutTransaction.addView(transactionCategoryText);
                if (t.getDescription() != null) {
                    TextView transactionDescriptionText = new TextView(this);
                    transactionDescriptionText.setText(t.getDescription());
                    linearLayoutTransaction.addView(transactionDescriptionText);
                }
                linearLayoutTransaction.addView(transactionAmtText);

                linearLayoutTransaction.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditTransactionActivity.class);
                    intent.putExtra("transactionId", t.getId());
                    startActivity(intent);
                });

                linearLayout.addView(linearLayoutTransaction);

                View viewDivider = new View(this);
                viewDivider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                viewDivider.setBackgroundColor(Color.parseColor("#000000"));
                linearLayout.addView(viewDivider);

            });

        });

    }

}