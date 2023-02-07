package com.team3.personalfinanceapp.insights;

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
import com.team3.personalfinanceapp.R;
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

    private List<Transaction> transactions;

    public CategorySpendFragment() {
        // Required empty public constructor
    }

    public CategorySpendFragment(List<Transaction> transactions) {
        this.transactions = transactions;
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

        createSpendingByCategory(view);
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

        return "This Month: " + "$" + Math.abs(currMonthSpend) + "\n"
                + "Last Month: " + "$" + Math.abs(prevMonthSpend) + "\n"
                + "Change: " + changeVsPrevMonth(currMonthSpend, prevMonthSpend);
    }

    private double changeVsPrevMonth(double currMonthSpend, double prevMonthSpend) {

        return Math.abs(currMonthSpend) - Math.abs(prevMonthSpend);
    }


}