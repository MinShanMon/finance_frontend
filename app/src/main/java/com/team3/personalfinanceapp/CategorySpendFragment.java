package com.team3.personalfinanceapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.team3.personalfinanceapp.model.Transaction;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategorySpendFragment extends Fragment {

    private APIInterface apiInterface;
    private ArrayList<Transaction> transactions;

    public CategorySpendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_spend, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();

        getTransactionsAndCreateSpendingByCategory(view);
    }

    /**
     * Retrieve all transactions and display the spending by category
     **/
    private void getTransactionsAndCreateSpendingByCategory(View view) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(1);
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.body() == null) {
                    call.cancel();
                    return;
                }
                transactions = new ArrayList<>(response.body());
                createSpendingByCategory(view);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }


    private void createSpendingByCategory(View view) {
        TextView foodInsights = view.findViewById(R.id.food_insights);
        TextView transportInsights = view.findViewById(R.id.transport_insights);
        TextView othersInsights = view.findViewById(R.id.others_insights);
        Month currMonth = Month.FEBRUARY;

        Map<String, Double> currMonthCategorySpending =
                transactions.stream().filter(t -> t.getDate().getMonth().equals(currMonth))
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Double> prevMonthCategorySpending =
                transactions.stream().filter(t -> t.getDate().getMonth().equals(currMonth.minus(1)))
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));

        foodInsights.setText(spendingDataText("Food", currMonthCategorySpending, prevMonthCategorySpending));
        transportInsights.setText(spendingDataText("Transport", currMonthCategorySpending, prevMonthCategorySpending));
        othersInsights.setText(spendingDataText("Others", currMonthCategorySpending, prevMonthCategorySpending));
    }

    private String spendingDataText(String category, Map<String, Double> currMonthSpendingMap, Map<String, Double> prevMonthSpendingMap) {
        double currMonthSpend = 0;
        double prevMonthSpend = 0;
        if (currMonthSpendingMap.containsKey(category)) {
            currMonthSpend = currMonthSpendingMap.get(category);
        }

        if (prevMonthSpendingMap.containsKey(category)) {
            prevMonthSpend = prevMonthSpendingMap.get(category);
        }

        return category + "\n"
                + "Current Month: " + currMonthSpend + "\n"
                + "Prev Month: " + prevMonthSpend + "\n"
                + "Change: " + pctChangeVsPrevMonth(currMonthSpend, prevMonthSpend);
    }

    private int pctChangeVsPrevMonth(double currMonthSpend, double prevMonthSpend) {
        if (prevMonthSpend == 0) {
            return 100;
        }
        return (int) (currMonthSpend / prevMonthSpend * 100 - 100);
    }


}