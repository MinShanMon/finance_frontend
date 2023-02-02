package com.team3.personalfinanceapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.team3.personalfinanceapp.CategorySpendFragment;
import com.team3.personalfinanceapp.PieChartFragment;

public class InsightsViewPagerAdapter extends FragmentStateAdapter {
    public InsightsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PieChartFragment();
            case 1:
                return new CategorySpendFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
