package com.team3.personalfinanceapp.transactions;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.BankResponse;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.statements.StatementsActivity;
import com.team3.personalfinanceapp.transactions.AddTransactionActivity;
import com.team3.personalfinanceapp.transactions.TransactionsActivity;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;
import com.team3.personalfinanceapp.utils.BankAPIInterface;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsFragment extends Fragment {

    private ArrayList<Transaction> transactions;

    private String moneyFormat;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        moneyFormat = getString(R.string.money_format);
        getTransactionsAndDisplayData(view);
        getAvailableBalanceAndDisplay(view);
        setViewAllButton(view);
        setViewStatementsBtn(view);
        setAddTransactionButton(view);

        TextView currentMonthHeader = view.findViewById(R.id.transaction_fragment_month);
        currentMonthHeader.setText(capitalize(LocalDate.now().getMonth()));
    }

    private String capitalize(Month month) {
        String monthStr = month.toString();
        return monthStr.substring(0, 1) + monthStr.substring(1).toLowerCase();
    }


    /**
     * Retrieve all transactions and display the data
     **/
    private void getTransactionsAndDisplayData(View view) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(1, 2);
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                TextView title = view.findViewById(R.id.largest_transaction_title);

                if (response.body() == null) {
                    call.cancel();
                    title.setText(R.string.empty_transaction_message);
                    return;
                }
                transactions = new ArrayList<>(response.body());
                displayLargestTransaction(view, transactions);
                displayIncome(view, transactions);
                displayExpenses(view, transactions);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    /** Set view other transactions button **/
    private void setViewAllButton(View view) {

        TextView viewAllBtn = view.findViewById(R.id.view_all_btn);
        viewAllBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TransactionsActivity.class);
            startActivity(intent);
        });
    }

    private void setViewStatementsBtn(View view) {

        TextView viewStatementBtn = view.findViewById(R.id.bank_statements_btn);
        viewStatementBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StatementsActivity.class);
            startActivity(intent);
        });
    }

    /** Set add transaction button **/
    private void setAddTransactionButton(View view) {
        Button addTransactionBtn = view.findViewById(R.id.add_transaction_btn);
        addTransactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            startActivity(intent);
        });
    }


    /**
     * Retrieve available balance from OCBC API and display
     **/
    private void getAvailableBalanceAndDisplay(View view) {
        BankAPIInterface bankAPIInterface = APIClient.getBankClient().create(BankAPIInterface.class);
        Call<BankResponse> bankResponseCall = bankAPIInterface.getAccountDetails(getString(R.string.ocbc_auth_header));
        bankResponseCall.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                if (response.isSuccessful()) {
                    BankResponse bankResponse = response.body();
                    if (bankResponse != null) {
                        double balance = bankResponse.getAvailableBalance();
                        TextView balanceTextView = view.findViewById(R.id.available_balance_amt);
                        balanceTextView.setText("$" + String.format(moneyFormat, balance));
                    }
                }
            }
            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void displayLargestTransaction(View view, ArrayList<Transaction> transactions) {
        Transaction latestTransaction = transactions.stream().max( (t1, t2) -> (int) (t2.getAmount() - t1.getAmount())).orElse(null);
        TextView title = view.findViewById(R.id.largest_transaction_title);
        if (latestTransaction == null) {
            title.setText("No transactions found");
            return;
        }
        title.setText(latestTransaction.getTitle());
        TextView amount = view.findViewById(R.id.largest_transaction_amount);
        String amountStr = "$" + String.format(moneyFormat, latestTransaction.getAmount());
        amount.setText(amountStr);
    }

    private void displayIncome(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream().filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyInView = view.findViewById(R.id.money_in_amount);
        moneyInView.setText("$" + String.format(moneyFormat, sum));
    }

    private void displayExpenses(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream().filter(t -> t.getAmount() < 0)
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyOutView = view.findViewById(R.id.money_out_amount);
        moneyOutView.setText("$" + String.format(moneyFormat, Math.abs(sum)));
    }

}