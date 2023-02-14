package com.team3.personalfinanceapp.insights;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.time.LocalDate;
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

    APIInterface apiInterface;
    SharedPreferences pref;



    public InsightsViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        apiInterface = APIClient.getClient().create(APIInterface.class);
        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
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
        getForecastAndSetLine();
    }

    private void getAllTransactionsAndSetCharts() {
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(pref.getInt("userid", 0), "Bearer " + pref.getString("token", ""));
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.body() != null) {
                    transactions = new ArrayList<>(response.body());
                    setPieChartAndCategorySpending();
                    setLineChart();
                    getForecastAndSetLine();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void getForecastAndSetLine() {

        Call<Map<String, Float>> getForecastCall =
                apiInterface.getSpendingForecastById(pref.getInt("userid", 0), pref.getString("token", ""));
        getForecastCall.enqueue(new Callback<Map<String, Float>>() {
            @Override
            public void onResponse(Call<Map<String, Float>> call, Response<Map<String, Float>> response) {
                Map<String, Float> forecastByMonth = response.body();
                setForecastLine(forecastByMonth);
            }

            @Override
            public void onFailure(Call<Map<String, Float>> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void setForecastLine(Map<String, Float> forecastByMonth) {
        List<Entry> entries = new ArrayList<>();
        if (transactions == null) {
            return;
        }
        int currentYear = transactions.get(transactions.size() - 1).getDate().getYear();

        List<Map.Entry<String, Float>> forecastDataList =
                forecastByMonth.entrySet().stream()
                .sorted((e1, e2) -> Integer.parseInt(e1.getKey()) - Integer.parseInt(e2.getKey()))
                .collect(Collectors.toList());
        forecastDataList.forEach( e -> {
            LocalDate date = LocalDate.of(currentYear, Integer.parseInt(e.getKey()), 1);
            long epochDay = date.toEpochDay();
            entries.add(new Entry(epochDay, e.getValue()));
        });


        LineDataSet lineDataSet = new LineDataSet(entries, "Spending Forecast");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.parseColor("magenta"));
        List<ILineDataSet> lineDataSets = lineChart.getLineData().getDataSets();
        lineDataSets.add(lineDataSet);
        lineChart.setData(new LineData(lineDataSets));
        lineChart.invalidate();
    }

    private void setPieChartAndCategorySpending() {
        InsightsViewPagerAdapter insightsPagerAdapter = new InsightsViewPagerAdapter(InsightsViewPagerFragment.this, transactions);
        viewPager.setAdapter(insightsPagerAdapter);
    }

    private void setLineChart() {

        configureLineChart(lineChart);
        Map<Long, Double> sumPerMonthMap = transactions.stream().collect(
                Collectors.groupingBy(
                        t -> t.getMonthYearEpoch(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        List<Entry> entries = new ArrayList<>();
        sumPerMonthMap.forEach((m, s) ->
                entries.add(new Entry(m, Math.abs(s.floatValue())))
        );

        // sort entries by date in ascending order
        Collections.sort(entries, (o1, o2) -> (int) (o1.getX() - o2.getX()));

        LineDataSet lineDataSet = new LineDataSet(entries, "Spending Trend");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.parseColor("#FF0000"));
        lineDataSet.setDrawFilled(false);
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
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                LocalDate epochToLocalDate = LocalDate.ofEpochDay((long) value);

                return epochToLocalDate.getMonth().toString().substring(0, 3) + " '"
                        + String.valueOf(epochToLocalDate.getYear()).substring(2, 4);
            }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

    }

}