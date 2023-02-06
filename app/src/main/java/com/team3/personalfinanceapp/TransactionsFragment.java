package com.team3.personalfinanceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3.personalfinanceapp.model.BankResponse;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.network.api.APIInterface;
import com.team3.personalfinanceapp.util.APIClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsFragment extends Fragment {

    private ArrayList<Transaction> transactions;
    private APIInterface apiInterface;

    private BankAPIInterface bankAPIInterface;

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
        getTransactionsAndDisplayData(view);
        getAvailableBalanceAndDisplay(view);

        TextView currentMonthHeader = view.findViewById(R.id.transaction_fragment_month);
        currentMonthHeader.setText(LocalDate.now().getMonth().toString());


        TextView viewAllBtn = view.findViewById(R.id.view_all_btn);
        viewAllBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TransactionsActivity.class);
            startActivity(intent);
        });

        Button addTransactionBtn = view.findViewById(R.id.add_transaction_btn);
        addTransactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            startActivity(intent);
        });
    }


    /**
     * Retrieve all transactions and display the data
     **/
    private void getTransactionsAndDisplayData(View view) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(1, 2);
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                TextView title = view.findViewById(R.id.latest_transaction_title);

                if (response.body() == null) {
                    call.cancel();
                    title.setText(R.string.empty_transaction_message);
                    return;
                }
                transactions = new ArrayList<>(response.body());
                displayLatestTransaction(view, transactions);
                displayMoneyIn(view, transactions);
                displayMoneyOut(view, transactions);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    /**
     * Retrieve available balance from OCBC API and display
     **/
    private void getAvailableBalanceAndDisplay(View view) {
        bankAPIInterface = APIClient.getBankClient().create(BankAPIInterface.class);
        Call<BankResponse> bankResponseCall = bankAPIInterface.getAccountDetails(getString(R.string.ocbc_auth_header));
        bankResponseCall.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                BankResponse bankResponse = response.body();

                double balance = bankResponse.getAvailableBalance();
                TextView balanceTextView = view.findViewById(R.id.available_balance_amt);
                balanceTextView.setText("$" + balance);

            }

            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void displayLatestTransaction(View view, ArrayList<Transaction> transactions) {
        Transaction latest_transaction = transactions.get(0);
        TextView title = view.findViewById(R.id.latest_transaction_title);
        title.setText(latest_transaction.getTitle());
        TextView amount = view.findViewById(R.id.latest_transaction_amount);
        String amountStr = "$" + latest_transaction.getAmount();
        amount.setText(amountStr);
    }

    private void displayMoneyIn(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream().filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyInView = view.findViewById(R.id.money_in_amount);
        moneyInView.setText("$" + sum);
    }

    private void displayMoneyOut(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream().filter(t -> t.getAmount() < 0)
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyOutView = view.findViewById(R.id.money_out_amount);
        moneyOutView.setText("$" + sum);
    }

}