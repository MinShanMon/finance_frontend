package com.team3.personalfinanceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.team3.personalfinanceapp.MainActivity;
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
    TextInputEditText current_password;
    TextInputLayout current_password_pwd;
    TextView txtLogin;
    AppCompatButton resetpassbtn;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    TextView error_msg;
    UserApi apiInterface;
    Intent intent;
    Boolean change_password;
    String otp;
    String email;
    String token;
    String passwordAp;
    String confirmP;
    String c_p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        resetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change_password){
                    change_pwd();
                }
                else{
                    reset(token);
                }

            }
        });
    }

    private void init(){
        apiInterface = APIClient.getClient().create(UserApi.class);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        current_password_pwd = findViewById(R.id.current_password_pwd);
        current_password = findViewById(R.id.current_password);
        resetpassbtn = findViewById(R.id.resetpassbtn);
        txtLogin = findViewById(R.id.txtLogin);
        error_msg = findViewById(R.id.error_msg);
        intent = getIntent();
        change_password = intent.getBooleanExtra("change_password", false);

        otp = intent.getStringExtra("otp");
        email = intent.getStringExtra("email");
        token = intent.getStringExtra("token");


        if(!change_password){
            current_password_pwd.setVisibility(View.GONE);
            txtLogin.setText("RESET PASSWORD");
        }
        else{
            txtLogin.setText("CHANGE PASSWORD");
        }
    }

    private void change_pwd(){
        passwordAp = password.getText().toString();
        confirmP = confirmPassword.getText().toString();
        c_p = current_password.getText().toString();
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String curr_pass = pref.getString("password", "");
        String tokens = pref.getString("token", "");
        Integer id = pref.getInt("userid", 0);
        if(c_p.isEmpty()){
            error_msg.setText("Fileds cannot be empty");
            return;
        }
        else if(!pref.getString("password", "").equals(c_p)){
            error_msg.setText("Current Password is incorrect");
            return;
        }
        if(passwordAp.isEmpty() && confirmP.isEmpty()){
            error_msg.setText("Fileds cannot be empty");
            return;
        }
        if(!passwordAp.equals(confirmP)){
            error_msg.setText("new confirmPassword and new password must be same");
            return;
        }
        Call<Object> userLoginCall = apiInterface.editPassword(id,passwordAp,"Bearer "+ tokens);
        userLoginCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                

//                editor = pref.edit();
//                editor.putString("password", passwordAp);
//
//                editor.commit();
//
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                error_msg.setText("");
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

    }

    private void reset(String tokens){
        passwordAp = password.getText().toString();
        confirmP = confirmPassword.getText().toString();
        c_p = current_password.getText().toString();

        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        if(passwordAp.isEmpty() && confirmP.isEmpty()){
            error_msg.setText("Fileds cannot be empty");
            return;
        }
        if(!passwordAp.equals(confirmP)){
            error_msg.setText("confirmPassword and password must be same");
            return;
        }

        Call<Object> userLoginCall = apiInterface.resetPassword(email,passwordAp,otp,"Bearer "+ tokens);
        userLoginCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

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