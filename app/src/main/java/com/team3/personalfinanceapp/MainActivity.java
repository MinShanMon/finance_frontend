package com.team3.personalfinanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.team3.personalfinanceapp.insights.InsightsViewPagerFragment;
import com.team3.personalfinanceapp.transactions.TransactionsFragment;
import com.team3.personalfinanceapp.profile_login.LoginActivity;
import com.team3.personalfinanceapp.profile_login.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private TransactionsFragment transactionsFragment = new TransactionsFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private InsightsViewPagerFragment insightsFragment = new InsightsViewPagerFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView btmNavBar = findViewById(R.id.btm_navbar);
        btmNavBar.setOnItemSelectedListener(this);
        btmNavBar.setSelectedItemId(R.id.manage_profile);
        getSupportActionBar().hide();

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

//        if(!pref.contains("token")){
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//        loginWithFb();


    }


    @Override
    public void onStart(){
        super.onStart();
        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        if(!pref.contains("token")){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return replaceFragment(item.getItemId());
    }

    private boolean replaceFragment(int itemId) {

        switch (itemId) {
            case R.id.home_item:

                commitTransaction(homeFragment);
                return true;
            case R.id.insights_item:

                commitTransaction(insightsFragment);
                return true;
            case R.id.transactions_item:

                commitTransaction(transactionsFragment);
                return true;
            case R.id.manage_profile:

                commitTransaction(profileFragment);
                return true;
            default:
                return false;
        }
    }

    private void commitTransaction(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }
}