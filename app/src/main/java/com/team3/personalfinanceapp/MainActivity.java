package com.team3.personalfinanceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.facebook.AccessToken;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.team3.personalfinanceapp.Fragment.HomeFragment;
import com.team3.personalfinanceapp.Fragment.ProductsFragment;
import com.team3.personalfinanceapp.insights.InsightsViewPagerFragment;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.profile_login.LoginActivity;
import com.team3.personalfinanceapp.profile_login.ProfileFragment;
import com.team3.personalfinanceapp.transactions.TransactionsFragment;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.UserApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, HomeFragment.IHomeFragment
, InsightsViewPagerFragment.IinsightFragment, ProductsFragment.IProductFragment, TransactionsFragment.ITransactionsFragment
, ProfileFragment.IProfileFragment{

    private TransactionsFragment transactionsFragment = new TransactionsFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private InsightsViewPagerFragment insightsFragment = new InsightsViewPagerFragment();
    private ProfileFragment profileFragment = new ProfileFragment();



    SharedPreferences pref;
    SharedPreferences.Editor editor;
    UserApi apiInterface;
    AccessToken accessToken;

    int active;
    BottomNavigationView btmNavBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btmNavBar = findViewById(R.id.btm_navbar);
        btmNavBar.setOnItemSelectedListener(this);
        btmNavBar.setSelectedItemId(R.id.home_item);

        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

        apiInterface = APIClient.getClient().create(UserApi.class);

        if(pref.contains("token") && pref.contains("userid")){
            checkToken(pref.getInt("userid",0),pref.getString("token",""));
        }
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            checkToken(pref.getInt("userid", 0), pref.getString("token", ""));
        }
    }


    @Override
    public void onStart(){
        super.onStart();
        btmNavBar.setSelectedItemId(active);
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        if(pref.contains("token") && pref.contains("userid")){
            checkToken(pref.getInt("userid",0),pref.getString("token",""));
        }
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            checkToken(pref.getInt("userid", 0), pref.getString("token", ""));
        }
    }

    private void checkToken(Integer uid, String token){
        Call<Token> userLoginCall = apiInterface.checkToken(uid, "Bearer " + token);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if(response.body() == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    return;
                }
    //                startActivity(new Intent(LoginActivity.this, MainActivity.class));
    //                finish();
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                call.cancel();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return replaceFragment(item.getItemId());
    }

    private boolean replaceFragment(int itemId) {

        switch (itemId) {

            case R.id.insights_item:
                commitTransaction(insightsFragment);
                return true;

            case R.id.products_item:
                ProductsFragment productsFragment = new ProductsFragment();
                commitTransaction(productsFragment);
                return true;

            case R.id.transactions_item:
                commitTransaction(transactionsFragment);
                return true;

            case R.id.manage_profile:
                commitTransaction(profileFragment);
                return true;

            default:
                commitTransaction(homeFragment);
                return true;
        }
    }

    private void commitTransaction(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    @Override
    public void viewDetailHome(int itemId) {
        btmNavBar.setSelectedItemId(itemId);
    }

    @Override
    public void viewDetailInsight(int itemId) {
        btmNavBar.setSelectedItemId(itemId);
    }


    @Override
    public void viewDetailProduct(int itemId) {
        btmNavBar.setSelectedItemId(itemId);
    }

    @Override
    public void viewDetailTransaction(int itemId) {
        btmNavBar.setSelectedItemId(itemId);
    }

    @Override
    public void viewDetailProfile(int itemId) {
        btmNavBar.setSelectedItemId(itemId);
    }
}