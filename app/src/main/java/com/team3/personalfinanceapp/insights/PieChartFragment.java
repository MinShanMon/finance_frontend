package com.team3.personalfinanceapp.insights;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
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


public class PieChartFragment extends Fragment {

    private List<Transaction> transactions;

    public PieChartFragment() {
        // Required empty public constructor
    }

    public PieChartFragment(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        createPieChart(view);
    }

    /**
     * Create Pie Chart
     **/
    private void createPieChart(View view) {
        PieChart pieChart = view.findViewById(R.id.transactions_piechart);
        configurePieChart(pieChart);
        setPieChartData(pieChart, transactions);
    }

    /**
     * Configure Pie Chart
     **/
    private void configurePieChart(PieChart pieChart) {

        pieChart.setTouchEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(0);

        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
    }

    /**
     * Add data set and respective colors to pie chart
     **/
    private void setPieChartData(PieChart pieChart, List<Transaction> transactions) {
        List<PieEntry> entries = new ArrayList<>();
        Map<String, Double> categoryTotalSpendMap =
                transactions.stream().collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)));

        categoryTotalSpendMap.forEach((category, spending) ->
                entries.add(new PieEntry(Math.abs(spending.floatValue()), category)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateXY(500, 500);
    }

}