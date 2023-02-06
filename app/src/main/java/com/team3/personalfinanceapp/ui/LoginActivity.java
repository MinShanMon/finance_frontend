package com.team3.personalfinanceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.network.api.UserApi;
import com.team3.personalfinanceapp.util.APIClient;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText txtUsername;
    TextInputEditText txtPassword;
    AppCompatButton btnLogin;
    AppCompatButton LoginWithFacebook;
    Button signUp;
    TextView txtForgetPassword;
    CallbackManager callbackManager;
    UserApi apiInterface;
    TextView error;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

        if (pref.contains("token"))
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
//            //STEP 2 Read from shared Preferences
//            boolean loginOk = logIn(pref.getString("username", ""), pref.getString("password", ""));
//
//            if (loginOk)
//            {
//                startProtectedActivity();
//            }

        }

        login();
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedLogin();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { register();
            }
        });
        LoginWithFacebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });
    }

    private void init(){
        txtUsername = findViewById(R.id.txtLoginUsername);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        error = findViewById(R.id.error_msg);
        signUp = findViewById(R.id.txtSignUp);
        LoginWithFacebook = findViewById(R.id.LoginWithFacebook);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

        apiInterface = APIClient.getClient().create(UserApi.class);
        callbackManager = CallbackManager.Factory.create();
    }

    private void login(){

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void register(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void clickedLogin() {
        if(txtPassword.getText().toString().isEmpty() && txtUsername.getText().toString().isEmpty()){
            error.setText("Field cannot empty");
            return;
        }
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        Call<Token> login = apiInterface.userLogin(username, password);
        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.body().access_token.equals("400")){
                    error.setText("Check Your Password Or Email");
                    return;
                }
                pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("email",username);
                editor.putString("password",password);
                editor.putString("token", response.body().refresh_token);
                editor.commit();
                fetchUserId(response.body().refresh_token, username);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("fail", "fail");
            }
        });
    }

    private void fetchUserId(String token, String email){

        Call<RegisteredUsers> userLoginCall = apiInterface.getUserInfo(email, "Bearer "+ token);
        userLoginCall.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {

                if(response.body().getStatus().equals("PENDING")){
                    sendEmail(email);
                }
                else{
                    pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putInt("userid", response.body().getId());
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<RegisteredUsers> call, Throwable t) {

            }
        });

    }

    private void sendEmail(String email){
        Call<Token> userLoginCall = apiInterface.sendOTPByEmail(email);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
              Intent intent = new Intent(LoginActivity.this, VerifyAccountActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", response.body().access_token);
                intent.putExtra("register", true);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }







}