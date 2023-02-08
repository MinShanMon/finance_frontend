package com.team3.personalfinanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.network.api.UserApi;
import com.team3.personalfinanceapp.ui.LoginActivity;
import com.team3.personalfinanceapp.ui.ProfileFragment;
import com.team3.personalfinanceapp.util.APIClient;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private TransactionsFragment transactionsFragment = new TransactionsFragment();
    private HomeFragment homeFragment = new HomeFragment();
    private InsightsViewPagerFragment insightsFragment = new InsightsViewPagerFragment();
    private ProfileFragment profileFragment = new ProfileFragment();


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    UserApi apiInterface;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView btmNavBar = findViewById(R.id.btm_navbar);
        btmNavBar.setOnItemSelectedListener(this);
        btmNavBar.setSelectedItemId(R.id.manage_profile);
        getSupportActionBar().hide();

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

            }
        });
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