package com.team3.personalfinanceapp;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsightsFragment extends Fragment {

    private ArrayList<Transaction> transactions;
    private APIInterface apiInterface;

    public InsightsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        getTransactionsAndSetChart(view);
    }



    /** Retrieve all transactions and display the pie chart **/
    private void getTransactionsAndSetChart(View view) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.transList(1);
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.body() == null) {
                    call.cancel();
                }
                transactions = new ArrayList<>(response.body());

                PieChart pieChart = view.findViewById(R.id.transactions_piechart);
                configurePieChart(pieChart);
                setPieChartData(pieChart, transactions);
                }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    /** Configure Pie Chart **/
    private void configurePieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.setTouchEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
    }
    /** Add data set and respective colors to pie chart **/
    private void setPieChartData(PieChart pieChart, ArrayList<Transaction> transactions) {
        List<PieEntry> entries = new ArrayList<>();
        Map<String, Double> categoryTotalSpendMap =
                transactions.stream().collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));

        categoryTotalSpendMap.forEach( (category, spending) ->
                entries.add(new PieEntry(Math.abs(spending.floatValue()), category)));

        PieDataSet dataSet = new PieDataSet(entries, "Spending By Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}