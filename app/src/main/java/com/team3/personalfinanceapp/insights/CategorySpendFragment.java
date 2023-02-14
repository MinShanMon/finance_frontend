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

import java.time.LocalDate;
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

    private String moneyFormat;

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
        moneyFormat = getString(R.string.money_format);
        createSpendingByCategory(view);
    }

    private void createSpendingByCategory(View view) {

        LocalDate currDate = LocalDate.now().withDayOfMonth(1);

        Map<String, Double> currMonthCategorySpending =
                transactions.stream().filter(t -> t.getDate().withDayOfMonth(1).equals(currDate))
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));

        Map<String, Double> prevMonthCategorySpending =
                transactions.stream().filter(t -> t.getDate().withDayOfMonth(1).equals(currDate.minusMonths(1)))
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));

        setSpendingDataText(view, currMonthCategorySpending, prevMonthCategorySpending);
    }

    private void setSpendingDataText(View view, Map<String, Double> currMonthSpendingMap, Map<String, Double> prevMonthSpendingMap) {

        TextView foodAmtThisMonth = view.findViewById(R.id.food_insights_thismonthamt);
        TextView foodAmtLastMonth = view.findViewById(R.id.food_insights_lastmonthamt);
        TextView foodAmtChange = view.findViewById(R.id.food_insights_changeamt);

        TextView transportAmtThisMonth = view.findViewById(R.id.transport_insights_thismonthamt);
        TextView transportAmtLastMonth = view.findViewById(R.id.transport_insights_lastmonthamt);
        TextView transportAmtChange = view.findViewById(R.id.transport_insights_changeamt);

        TextView othersAmtThisMonth = view.findViewById(R.id.others_insights_thismonthamt);
        TextView othersAmtLastMonth = view.findViewById(R.id.others_insights_lastmonthamt);
        TextView othersAmtChange = view.findViewById(R.id.others_insights_changeamt);

        double[] foodSpending = new double[2];
        double[] transportSpending = new double[2];
        double[] othersSpending = new double[2];

        currMonthSpendingMap.forEach((cat, spend) -> {
            if (cat.equalsIgnoreCase("food")) {
                foodAmtThisMonth.setText("$" + String.format(moneyFormat, spend));
                foodSpending[0] = spend;
            }
            if (cat.equalsIgnoreCase("transport")) {
                transportAmtThisMonth.setText("$" + String.format(moneyFormat, spend));
                transportSpending[0] = spend;
            }
            if (cat.equalsIgnoreCase("others")) {
                othersAmtThisMonth.setText("$" + String.format(moneyFormat, spend));
                othersSpending[0] = spend;
            }
        });

        prevMonthSpendingMap.forEach((cat, spend) -> {
            if (cat.equalsIgnoreCase("food")) {
                foodAmtLastMonth.setText("$" + String.format(moneyFormat, spend));
                foodSpending[1] = spend;
            }
            if (cat.equalsIgnoreCase("transport")) {
                transportAmtLastMonth.setText("$" + String.format(moneyFormat, spend));
                transportSpending[1] = spend;
            }
            if (cat.equalsIgnoreCase("others")) {
                othersAmtLastMonth.setText("$" + String.format(moneyFormat, spend));
                othersSpending[1] = spend;
            }
        });

        setChangeText(foodAmtChange, foodSpending);
        setChangeText(transportAmtChange, transportSpending);
        setChangeText(othersAmtChange, othersSpending);

    }

    private void setChangeText(TextView changeText, double[] spending) {
        double spendingChange = spending[0] - spending[1];

        if (spendingChange > 0) {
            changeText.setText("$" + "+" + String.format(moneyFormat, spendingChange));
            changeText.setTextColor(Color.RED);
        } else if (spendingChange < 0) {
            changeText.setText("$" + String.format(moneyFormat, spendingChange));
            changeText.setTextColor(Color.GREEN);
        }
    }


}