package com.team3.personalfinanceapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {


    private List<Transaction> transactions;

    private SharedPreferences pref;
    private int userId;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        pref = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        userId = pref.getInt("userid", 0);
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(userId,
                LocalDate.now().getMonth().getValue(), "Bearer "+ pref.getString("token", ""));
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                transactions = response.body();
                int totalSpendingThisMonth = (int) transactions.stream().filter(t -> t.getAmount() < 0)
                        .mapToDouble(Transaction::getAmount)
                        .reduce(Double::sum).orElse(0);
                ProgressBar budgetBar = view.findViewById(R.id.budget_progress_bar);
                SharedPreferences budgetPref = getActivity().getSharedPreferences("user_budget", Context.MODE_PRIVATE);
                int max = (int) budgetPref.getFloat(String.valueOf(userId), 0);

                if (max > 0) {
                    budgetBar.setMax(max);
                    budgetBar.setProgress(Math.abs(totalSpendingThisMonth));
                } else {
                    budgetBar.setVisibility(View.GONE);
                    TextView noBudgetText = new TextView(getContext());
                    LinearLayout linearLayout = view.findViewById(R.id.budget_card_layout);
                    noBudgetText.setText("No budget set, please set one.");
                    linearLayout.addView(noBudgetText);
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });
    }
}