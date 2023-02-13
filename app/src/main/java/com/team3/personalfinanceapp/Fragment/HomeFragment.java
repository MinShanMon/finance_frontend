package com.team3.personalfinanceapp.Fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SetBudgetDialogFragment.setBudgetListener {


    private List<Transaction> transactions;

    private SharedPreferences pref;
    private int userId;

    private int totalSpendingThisMonth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        setBudgetButton(view);
        setSpendingForecastButton(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        pref = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        userId = pref.getInt("userid", 0);
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(userId,
                LocalDate.now().getMonth().getValue(), "Bearer "+ pref.getString("token", ""));
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                transactions = response.body();
                totalSpendingThisMonth = (int) transactions.stream().filter(t -> t.getAmount() < 0)
                        .mapToDouble(Transaction::getAmount)
                        .reduce(Double::sum).orElse(0);
                setBudgetBar(totalSpendingThisMonth);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void setBudgetBar(int totalSpendingThisMonth) {
        View view = getView();
        ProgressBar budgetBar = view.findViewById(R.id.budget_progress_bar);
        SharedPreferences budgetPref = getActivity().getSharedPreferences("user_budget", Context.MODE_PRIVATE);
        int max = (int) budgetPref.getFloat(String.valueOf(userId), 0);

        if (max > 0) {
            TextView budgetAmtText = view.findViewById(R.id.budget_amt_progressbar);
            budgetAmtText.setText("$" + max);
            budgetBar.setMax(max);
            ObjectAnimator.ofInt(budgetBar, "progress", Math.abs(totalSpendingThisMonth))
                    .setDuration(1000)
                    .start();
        } else {
            budgetBar.setVisibility(View.GONE);
            TextView noBudgetText = new TextView(getContext());
            LinearLayout linearLayout = view.findViewById(R.id.budget_card_layout);
            noBudgetText.setText("No budget set, please set one.");
            linearLayout.addView(noBudgetText);
        }
    }

    private void setBudgetButton(View view) {
        Button setBudgetBtn = view.findViewById(R.id.set_budget_btn);
        setBudgetBtn.setOnClickListener(v ->
                new SetBudgetDialogFragment().show(getParentFragmentManager(), "budget"));
    }

    @Override
    public void onDialogPositiveClick() {
        setBudgetBar(totalSpendingThisMonth);
    }

    private void setSpendingForecastButton(View view) {
        Button forecastBtn = view.findViewById(R.id.get_forecast_btn);
        forecastBtn.setOnClickListener( v -> {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<Map<String,Float>> getForecastCall =  apiInterface.getSpendingForecastById(userId, "Bearer "+ pref.getString("token", ""));
            getForecastCall.enqueue(new Callback<Map<String, Float>>() {
                @Override
                public void onResponse(Call<Map<String, Float>> call, Response<Map<String, Float>> response) {
                    Map<String, Float> forecastByMonth = response.body();
                    setForecastLineChart(forecastByMonth, view);
                    System.out.println(forecastByMonth);
                }

                @Override
                public void onFailure(Call<Map<String, Float>> call, Throwable t) {
                    call.cancel();
                }
            });
        });
    }

    private void setForecastLineChart(Map<String, Float> spendingForecastByMonth, View view) {
        LineChart forecastChart = view.findViewById(R.id.forecast_chart);

    }
}