package com.team3.personalfinanceapp.profile_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.utils.UserApi;
import com.team3.personalfinanceapp.utils.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPasswordActivity extends AppCompatActivity {


    TextInputEditText txtForgetPassword;
    AppCompatButton sendOTP;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    UserApi apiInterface;
    String email;
    TextView error_msg;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
//        if(!pref.contains("token")){
//            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
        init();
        sendOTP.setOnClickListener(v -> {
            email = txtForgetPassword.getText().toString();
            error_msg.setText("");
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                error_msg.setText("please enter valid email");
                return;
            }
            sendEmail(email);
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        sendOTP.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        txtForgetPassword.setEnabled(true);
    }

    private void init(){
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        error_msg = findViewById(R.id.error_msg);
        sendOTP = findViewById(R.id.sendOTP);
        apiInterface = APIClient.getClient().create(UserApi.class);
        progressBar = findViewById(R.id.barrr);
        progressBar.setVisibility(View.INVISIBLE);
//        editor = pref.edit();
//        editor.clear();
//        editor.commit();
//        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
//        editor = pref.edit();
    }

    private void sendEmail(String emails){
        sendOTP.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        txtForgetPassword.setEnabled(false);
        Call<Token> userLoginCall = apiInterface.sendOTPByEmail(emails);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.i("here", "success");
                if(response.body().access_token.equals("400")){
                    error_msg.setText("Email Not Exit In Database");
                    sendOTP.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    txtForgetPassword.setEnabled(true);
                    return;
                }

                txtForgetPassword.setEnabled(false);
                Intent intent = new Intent(ForgetPasswordActivity.this, VerifyAccountActivity.class);
                error_msg.setText("");
                intent.putExtra("email", emails);
                intent.putExtra("token", response.body().access_token);
                startActivity(intent);
            }


            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("here", "fail");

//                Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordActivity.class);
//                startActivity(intent);


            }
        });
    }




}