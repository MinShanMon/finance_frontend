package com.team3.personalfinanceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.network.api.UserApi;
import com.team3.personalfinanceapp.util.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtRegisterUsername;
    TextInputEditText edtRegisterEmail;
    TextInputEditText edtRegisterPassword;
    TextInputEditText edtRegisterConfirmPassword;
    AppCompatButton btnRegister;
    TextView error_msg;
    RegisteredUsers user;
    UserApi apiInterface;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView img_backArrow;

    String email;
    String username;
    String password;
    String confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        img_backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init(){
        edtRegisterUsername = findViewById(R.id.edtRegisterUsername);
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        img_backArrow = findViewById(R.id.img_backArrow);
        error_msg = findViewById(R.id.error_msg);
        btnRegister = findViewById(R.id.btnRegister);
        apiInterface = APIClient.getClient().create(UserApi.class);
    }

    private void register(){

        user = new RegisteredUsers();
        email = edtRegisterEmail.getText().toString();
        username = edtRegisterUsername.getText().toString();
        password = edtRegisterPassword.getText().toString();
        confirmPassword = edtRegisterConfirmPassword.getText().toString();
        user.setEmail(email);
        user.setFullName(username);
        user.setPassword(confirmPassword);

        if(email.isEmpty() && username.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()){
            error_msg.setText("Fileds cannot be empty");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            error_msg.setText("");
            error_msg.setText("please enter valid email");
            return;
        }
        if(!password.equals(confirmPassword)){
            error_msg.setText("");
            error_msg.setText("confirmPassword and password must be same");
            return;
        }

        Call<RegisteredUsers> userLoginCall = apiInterface.regUser(user);
        userLoginCall.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {

                editor = pref.edit();
                editor.clear();
                editor.commit();
                pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("email",email);
                editor.putString("password",password);
                editor.putInt("userid", response.body().getId());
                editor.commit();
                sendEmail(response.body().getEmail());
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

//                editor.putString("token", response.body().access_token);



                Intent intent = new Intent(RegisterActivity.this, VerifyAccountActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", response.body().access_token);
                intent.putExtra("register",true);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }






}