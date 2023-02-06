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
import com.team3.personalfinanceapp.ui.LoginActivity;
import com.team3.personalfinanceapp.ui.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

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
        btmNavBar.setSelectedItemId(R.id.home_item);

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

//        if(!pref.contains("token")){
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//        loginWithFb();


    }

    private void loginWithFb(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code 1319406795267973
                        Gson gson = new Gson();
                        String json = gson.toJson(response);
                        Log.i("response",json);

                        try {
                            String fullname = object.getString("name");
                            //setting profile picture
//                            String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
//                            Picasso.get().load(url).into(imageView);
//                            txt.setText(fullname);
//                            Log.i("response",fullname);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link, picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
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