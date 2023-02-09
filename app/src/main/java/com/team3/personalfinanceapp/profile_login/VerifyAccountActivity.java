package com.team3.personalfinanceapp.profile_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.utils.UserApi;
import com.team3.personalfinanceapp.utils.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAccountActivity extends AppCompatActivity {

    TextView verify_account;
    UserApi apiInterface;
    SharedPreferences pref;
    EditText edtCodeSix;
    SharedPreferences.Editor editor;
    TextView txtVerifyEmail;
    String email;
    TextView error_msg;
    ImageView img_backArrow;
    TextView txtGetVerify;
    String token;
    Boolean register;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);
//        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
//        String uid = pref.getString("token", "");
//        Log.i("verify token", uid);
        init();
        img_backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        verify_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_otp();
            }
        });
        txtGetVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(email);
            }
        });
    }

    private void init(){
        verify_account = findViewById(R.id.verify_account);
        img_backArrow = findViewById(R.id.img_backArrow);
        txtGetVerify = findViewById(R.id.txtGetVerify);
        apiInterface = APIClient.getClient().create(UserApi.class);
        edtCodeSix = findViewById(R.id.edtCodeSix);
        txtVerifyEmail = findViewById(R.id.txtVerifyEmail);
        intent = getIntent();
        email = intent.getStringExtra("email");
        token=intent.getStringExtra("token");
        register = intent.getBooleanExtra("register", false);
        txtVerifyEmail.setText(email);
        error_msg = findViewById(R.id.error_msg);
    }



    private void verify_otp(){
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String otp = edtCodeSix.getText().toString();
        Call<Token> userLoginCall = apiInterface.verifyOTP(email, otp, "Bearer "+ token);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if(response.body().status.equals("0")){
                    if(!register){
                        Log.i("here", "work");
                        Intent intents = new Intent(VerifyAccountActivity.this, ChangePasswordActivity.class);
                        intents.putExtra("otp", otp);
                        intents.putExtra("email", email);
                        intents.putExtra("token", response.body().refresh_token);
                        error_msg.setText("");
                        startActivity(intents);
                        finish();
                    }
                    else{
                        //register user
                        Log.i("regi", "work");
                        editor = pref.edit();
                        editor.putString("token", response.body().refresh_token);
                        editor.commit();
                        Intent intentss = new Intent(VerifyAccountActivity.this, MainActivity.class);
//                        intent.putExtra("otp", otp);
                        error_msg.setText("");
                        startActivity(intentss);
                        finish();
                    }
                }

                else if(response.body().status.equals("1")){
                    //wrong otp
                    Log.i("wrong otp", "here");
                    error_msg.setText("Wrong Otp");
                    return;
                }

                else if(response.body().status.equals("2")){
                    //otp time out
                    Log.i("time out", "here");
                    error_msg.setText("Otp Time Out Request Again");
                    return;
                }

                else{
                    editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    //smth wrong
                    Intent intent = new Intent(VerifyAccountActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("fail", "fail");
            }
        });
    }

    private void sendEmail(String emails) {
        Call<Token> userLoginCall = apiInterface.sendOTPByEmail(emails);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.i("here", "success");
                    token= response.body().access_token;
                    error_msg.setText("");
                    return;
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