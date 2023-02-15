package com.team3.personalfinanceapp.transactions;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3.personalfinanceapp.Fragment.ProductsFragment;
import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.BankResponse;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.statements.LinkBankActivity;
import com.team3.personalfinanceapp.statements.StatementsActivity;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;
import com.team3.personalfinanceapp.utils.BankAPIInterface;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsFragment extends Fragment {

    private ArrayList<Transaction> transactions;

    private String moneyFormat;
    private SharedPreferences pref;
    private ITransactionsFragment iTransactionsFragment;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        setupOnBackPressed();
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    private void setupOnBackPressed(){
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(isEnabled()){

                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewDetailProduct(R.id.transactions_item);
        View view = getView();
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        moneyFormat = getString(R.string.money_format);
        getTransactionsAndDisplayData(view);
        getAvailableBalanceAndDisplay(view);
        setViewAllButton(view);
        setViewStatementsBtn(view);
        setAddTransactionButton(view);
        setLinkBankButton(view);

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
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(pref.getInt("userid", 0), LocalDate.now().getMonth().getValue(), "Bearer " + pref.getString("token", ""));
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

    /**
     * Set view other transactions button
     **/
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

    /**
     * Set add transaction button
     **/
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
        TextView balanceTextView = view.findViewById(R.id.available_balance_amt);
        BankAPIInterface bankAPIInterface = APIClient.getBankClient().create(BankAPIInterface.class);
        SharedPreferences bankPref = this.getActivity().getSharedPreferences("user_banklist", MODE_PRIVATE);
        Set<String> bankAccountSet =
                bankPref.getStringSet(String.valueOf(pref.getInt("userid", 0)), new HashSet<>());

        if (bankAccountSet.isEmpty()) {
            balanceTextView.setText("No bank account linked");
            balanceTextView.setTextSize(30);
            return;
        }

        String[] bankAccts = bankAccountSet.toArray(new String[0]);
        String[] bankDetail = bankAccts[0].split(":");

        if (bankDetail.length == 0) {
            balanceTextView.setText("No bank account linked");
            balanceTextView.setTextSize(30);
            return;
        }

        Call<BankResponse> bankResponseCall =
                bankAPIInterface.getAccountDetails(getString(R.string.ocbc_auth_header), bankDetail[1]);
        bankResponseCall.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                if (response.isSuccessful()) {
                    BankResponse bankResponse = response.body();
                    if (bankResponse != null) {
                        double balance = bankResponse.getAvailableBalance();

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
        TextView amount = view.findViewById(R.id.largest_transaction_amount);
        Transaction largestTransaction = transactions.stream()
                .filter(t -> !t.getCategory().equalsIgnoreCase("income"))
                .filter(t -> t.getDate().withDayOfMonth(1).equals(LocalDate.now().withDayOfMonth(1)))
                .max((t1, t2) -> (int) (t1.getAmount() - t2.getAmount())).orElse(null);
        TextView title = view.findViewById(R.id.largest_transaction_title);
        if (largestTransaction == null) {
            title.setText("No transactions found for this month");
            amount.setText("");
            return;
        }
        title.setText(largestTransaction.getTitle());
        String amountStr = "$" + String.format(moneyFormat, largestTransaction.getAmount());
        amount.setText(amountStr);
    }

    private void displayIncome(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase("income"))
                .filter(t -> t.getDate().withDayOfMonth(1).equals(LocalDate.now().withDayOfMonth(1)))
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyInView = view.findViewById(R.id.money_in_amount);
        moneyInView.setText("$" + String.format(moneyFormat, sum));
    }

    private void displayExpenses(View view, ArrayList<Transaction> transactions) {
        Double sum = transactions.stream()
                .filter(t -> !t.getCategory().equalsIgnoreCase("income"))
                .filter(t -> t.getDate().withDayOfMonth(1).equals(LocalDate.now().withDayOfMonth(1)))
                .mapToDouble(Transaction::getAmount)
                .reduce(Double::sum).orElse(0);
        TextView moneyOutView = view.findViewById(R.id.money_out_amount);
        moneyOutView.setText("$" + String.format(moneyFormat, Math.abs(sum)));
    }

    private void setLinkBankButton(View view) {
        Button linkBankBtn = view.findViewById(R.id.link_bank_btn);
        linkBankBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), LinkBankActivity.class)));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iTransactionsFragment = (ITransactionsFragment) context;
    }

    void viewDetailProduct(int itemId) {
        iTransactionsFragment.viewDetailTransaction(itemId);
    }
    public interface ITransactionsFragment {
        void viewDetailTransaction(int itemId);
    }

}