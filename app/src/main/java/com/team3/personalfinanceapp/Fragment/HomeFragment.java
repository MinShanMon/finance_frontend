package com.team3.personalfinanceapp.Fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SetBudgetDialogFragment.setBudgetListener {


    private List<Transaction> transactions;

    private SharedPreferences pref;
    private int userId;

    private int totalSpendingThisMonth;

    String moneyFormat;

    private View view;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        moneyFormat = getString(R.string.money_format);
        setBudgetButton(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        pref = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        userId = pref.getInt("userid", 0);
        Call<List<Transaction>> transactionsCall = apiInterface.getTransactionsByMonth(userId,
                LocalDate.now().getMonth().getValue(), "Bearer " + pref.getString("token", ""));
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                transactions = response.body();
                totalSpendingThisMonth = transactions.stream()
                        .filter(t -> !t.getCategory().equalsIgnoreCase("income"))
                        .map(t -> t.getAmount()).reduce(Double::sum)
                        .orElse(Double.valueOf(0)).intValue();
                setBudgetBar(totalSpendingThisMonth);
                setCategorySpend(getView());
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });

        Call<Map<String, Float>> getForecastCall =
                apiInterface.getSpendingForecastById(pref.getInt("userid", 0), "Bearer " + pref.getString("token", ""));
        getForecastCall.enqueue(new Callback<Map<String, Float>>() {
            @Override
            public void onResponse(Call<Map<String, Float>> call, Response<Map<String, Float>> response) {
                Map<String, Float> forecastByMonth = response.body();
                if (forecastByMonth != null) {
                    Float forecastAmt = forecastByMonth.getOrDefault(String.valueOf(LocalDate.now().getMonth().getValue()), Float.valueOf(0));
                    TextView forecast = view.findViewById(R.id.this_month_forecast);
                    forecast.setText("$" + String.format(view.getResources().getString(R.string.money_format), forecastAmt));
                }
            }
            @Override
            public void onFailure(Call<Map<String, Float>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void setBudgetBar(int totalSpendingThisMonth) {
        ProgressBar budgetBar = view.findViewById(R.id.budget_progress_bar);
        TextView budgetAmtText = view.findViewById(R.id.budget_amt_progressbar);
        TextView progressBarStartLabel = view.findViewById(R.id.progress_bar_startlabel);
        SharedPreferences budgetPref = getActivity().getSharedPreferences("user_budget", Context.MODE_PRIVATE);
        int max = (int) budgetPref.getFloat(String.valueOf(userId), 0);

        if (max > 0) {
            budgetAmtText.setText("$" + max);
            progressBarStartLabel.setText("$0");
            budgetBar.setMax(max);
            budgetBar.setVisibility(View.VISIBLE);
            ObjectAnimator.ofInt(budgetBar, "progress", Math.abs(totalSpendingThisMonth))
                    .setDuration(1000)
                    .start();
        } else {
            budgetBar.setVisibility(View.GONE);
            budgetAmtText.setText("");
            progressBarStartLabel.setText("No budget set, please set one.");
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

    private void setCategorySpend(View view) {
        TextView foodSpendingText = view.findViewById(R.id.food_spending);
        foodSpendingText.setText("$" + String.format(moneyFormat, getCategorySpend("food")));

        TextView transportSpendingText = view.findViewById(R.id.transport_spending);
        transportSpendingText.setText("$" + String.format(moneyFormat, getCategorySpend("transport")));

        TextView othersSpendingText = view.findViewById(R.id.others_spending);
        othersSpendingText.setText("$" + String.format(moneyFormat, getCategorySpend("others")));
    }

    private Double getCategorySpend(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .map(t -> t.getAmount())
                .reduce(Double::sum).orElse((double) 0);
    }
}