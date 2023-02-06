package com.team3.personalfinanceapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team3.personalfinanceapp.CategorySpendFragment;
import com.team3.personalfinanceapp.ErrorFragment;
import com.team3.personalfinanceapp.PieChartFragment;
import com.team3.personalfinanceapp.model.Transaction;

import java.util.List;

public class InsightsViewPagerAdapter extends FragmentStateAdapter {

    private List<Transaction> transactions;
    public InsightsViewPagerAdapter(@NonNull Fragment fragment, List<Transaction> transactions) {
        super(fragment);
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
    public int getItemCount() {
        return 2;
    }
}
