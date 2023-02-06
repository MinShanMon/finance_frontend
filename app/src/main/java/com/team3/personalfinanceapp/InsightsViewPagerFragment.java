package com.team3.personalfinanceapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.team3.personalfinanceapp.adapter.InsightsViewPagerAdapter;
import com.team3.personalfinanceapp.model.Transaction;
import com.team3.personalfinanceapp.network.api.APIInterface;
import com.team3.personalfinanceapp.util.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsightsViewPagerFragment extends Fragment {


    private ViewPager2 viewPager;

    private FragmentStateAdapter insightsPagerAdapter;

    private ArrayList<Transaction> transactions;

    private APIInterface apiInterface;

    private TabLayout tabLayout;

    private TabLayoutMediator tabLayoutMediator;


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

        tabLayout = view.findViewById(R.id.insights_tablayout);
        viewPager = view.findViewById(R.id.insights_viewpager);
        viewPager.setAdapter(new InsightsViewPagerAdapter(this, new ArrayList<>()));
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText("Pie Chart");
                    else if (position == 1)
                        tab.setText("Spending By Category");
                }
        );
        tabLayoutMediator.attach();
        getAllTransactionsAndSetCharts(view);
    }

    private void getAllTransactionsAndSetCharts(View view) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Transaction>> transactionsCall = apiInterface.getAllTransactions(1); //hardcoded userid
        transactionsCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                transactions = new ArrayList<>(response.body());
                setPieChartAndCategorySpending();
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error, please try again", Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    private void setPieChartAndCategorySpending() {
        insightsPagerAdapter = new InsightsViewPagerAdapter(InsightsViewPagerFragment.this, transactions);
        viewPager.setAdapter(insightsPagerAdapter);
    }

    private void setLineChart(View view) {
        LineChart lineChart = view.findViewById(R.id.insights_linechart);

    }

}