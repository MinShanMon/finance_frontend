package com.team3.personalfinanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView btmNavBar = findViewById(R.id.btm_navbar);
        btmNavBar.setOnItemSelectedListener(this);
        btmNavBar.setSelectedItemId(R.id.home_item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item != null) {
            return replaceFragment(item.getItemId());
        }
        return false;
    }

    private boolean replaceFragment(int itemId) {

        switch (itemId) {
            case R.id.home_item:
                HomeFragment homeFragment = new HomeFragment();
                commitTransaction(homeFragment);
                return true;
            case R.id.insights_item:
                InsightsViewPagerFragment insightsFragment = new InsightsViewPagerFragment();
                commitTransaction(insightsFragment);
                return true;
            case R.id.transactions_item:
                TransactionsFragment transactionsFragment = new TransactionsFragment();
                commitTransaction(transactionsFragment);
                return true;
        }
        return false;
    }
    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }
}