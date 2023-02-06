package com.team3.personalfinanceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.network.api.UserApi;
import com.team3.personalfinanceapp.util.APIClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText password;
    TextInputEditText confirmPassword;
    AppCompatButton resetpassbtn;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    TextView error_msg;
    UserApi apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        resetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

    }

    private void init(){
        apiInterface = APIClient.getClient().create(UserApi.class);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        resetpassbtn = findViewById(R.id.resetpassbtn);
        error_msg = findViewById(R.id.error_msg);
    }

    private void reset(){
        Intent intent = getIntent();
        String otp = intent.getStringExtra("otp");
        String email = intent.getStringExtra("email");
        String token = intent.getStringExtra("token");
        String passwordAp = password.getText().toString();
        String confirmP = confirmPassword.getText().toString();

        if(passwordAp.isEmpty() && confirmP.isEmpty()){
            error_msg.setText("Fileds cannot be empty");
            return;
        }
        if(!passwordAp.equals(confirmP)){
            error_msg.setText("confirmPassword and password must be same");
            return;
        }
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        Call<Object> userLoginCall = apiInterface.resetPassword(email,passwordAp,otp,"Bearer "+ token);
        userLoginCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

//                pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
//                editor = pref.edit();
//                editor.putString("token", response.body().access_token);
//
//                editor.commit();
//
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                error_msg.setText("");
                editor = pref.edit();
                editor.clear();
                editor.commit();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }
}