package com.team3.personalfinanceapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team3.personalfinanceapp.insights.CategorySpendFragment;
import com.team3.personalfinanceapp.ErrorFragment;
import com.team3.personalfinanceapp.insights.InsightsViewPagerFragment;
import com.team3.personalfinanceapp.insights.PieChartFragment;
import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

public class InsightsViewPagerAdapter extends FragmentStateAdapter {

    private List<Transaction> transactions;

    InsightsViewPagerFragment fragment;
    public InsightsViewPagerAdapter(@NonNull Fragment fragment, List<Transaction> transactions) {
        super(fragment);
        this.fragment = (InsightsViewPagerFragment) fragment;
        this.transactions = transactions;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PieChartFragment(transactions);
        } else if (position == 1) {
            return new CategorySpendFragment(transactions);
        }
        return new ErrorFragment();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
