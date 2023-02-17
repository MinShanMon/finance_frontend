package com.team3.personalfinanceapp.insights;

import android.content.Context;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;
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
        setPieChartData(pieChart);
    }

    /**
     * Configure Pie Chart
     **/
    private void configurePieChart(PieChart pieChart) {

        pieChart.setTouchEnabled(false);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(0);
        pieChart.setNoDataText("Loading...");

        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(52f);
        pieChart.setTransparentCircleRadius(55f);
    }

    /**
     * Add data set and respective colors to pie chart
     **/
    private void setPieChartData(PieChart pieChart) {
        List<PieEntry> entries = new ArrayList<>();
        Map<String, Double> categoryTotalSpendMap =
                transactions.stream()
                        .filter(t -> !t.getCategory().equalsIgnoreCase("income"))
                        .filter(t -> t.getDate().withDayOfMonth(1).equals(LocalDate.now().withDayOfMonth(1)))
                        .collect(Collectors.groupingBy(Transaction::getCategory,
                                Collectors.summingDouble(Transaction::getAmount)));

        categoryTotalSpendMap.forEach((category, spending) ->
                entries.add(new PieEntry(Math.abs(spending.floatValue()), category)));

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No transactions for this month");
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.parseColor("#B74Bff"), Color.parseColor("#7D77EE"), Color.parseColor("#FF754B")});

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        setCenterText(pieChart);
        pieChart.invalidate();
        pieChart.animateXY(500, 500);
    }

    private void setCenterText(PieChart pieChart) {
        double sum;
        if (transactions.isEmpty()) {
            sum = 0;
        } else {
            sum = transactions.stream()
                    .filter(t -> !t.getCategory().equalsIgnoreCase("income"))
                    .filter(t -> t.getDate().withDayOfMonth(1).equals(LocalDate.now().withDayOfMonth(1)))
                    .mapToDouble(Transaction::getAmount)
                    .reduce(Double::sum).orElse((double) 0);
        }
        pieChart.setCenterText("This Month\n$" + String.format(getString(R.string.money_format), Math.abs(sum)));
    }

}