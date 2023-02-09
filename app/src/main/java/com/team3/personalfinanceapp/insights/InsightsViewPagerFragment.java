package com.team3.personalfinanceapp.insights;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.adapter.InsightsViewPagerAdapter;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.APIInterface;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsightsViewPagerFragment extends Fragment {


    private ViewPager2 viewPager;

    private ArrayList<Transaction> transactions;

    LineChart lineChart;


    public InsightsViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insights_view_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.insights_linechart);
        lineChart.setNoDataText("Loading...");

        TabLayout tabLayout = view.findViewById(R.id.insights_tablayout);
        viewPager = view.findViewById(R.id.insights_viewpager);
        viewPager.setAdapter(new InsightsViewPagerAdapter(this, new ArrayList<>()));
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText("Pie Chart");
                    else if (position == 1)
                        tab.setText("Spending By Category");
                }
        );
        tabLayoutMediator.attach();
        getAllTransactionsAndSetCharts();

    }

    private void getAllTransactionsAndSetCharts() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        SharedPreferences pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(pref.getInt("userid", 0), "Bearer "+ pref.getString("token" , ""));
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.body() != null) {
                    transactions = new ArrayList<>(response.body());
                    setPieChartAndCategorySpending();
                    setLineChart();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void setPieChartAndCategorySpending() {
        InsightsViewPagerAdapter insightsPagerAdapter = new InsightsViewPagerAdapter(InsightsViewPagerFragment.this, transactions);
        viewPager.setAdapter(insightsPagerAdapter);
    }

    private void setLineChart() {

        configureLineChart(lineChart);
        Map<Month, Double> sumPerMonthMap = transactions.stream().collect(
                Collectors.groupingBy(
                        t -> t.getDate().getMonth(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        List<Entry> entries = new ArrayList<>();
        sumPerMonthMap.forEach((m, s) ->
                entries.add(new Entry(m.getValue(), Math.abs(s.floatValue())))
        );

        // sort entries by month in ascending order
        Collections.sort(entries, (o1, o2) -> (int) (o1.getX() - o2.getX()));

        LineDataSet lineDataSet = new LineDataSet(entries, "Spending Trend");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSet);
        LineData lineData = new LineData(lineDataSets);
        lineChart.setData(lineData);
        setXLabels(lineChart);
        lineChart.invalidate();
    }

    private void configureLineChart(LineChart lineChart) {
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);

    }

    private void setXLabels(LineChart lineChart) {

        Month[] months = Month.values();
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[(int) value - 1].toString();
            }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

    }

}