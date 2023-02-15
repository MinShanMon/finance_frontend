package com.team3.personalfinanceapp.profile_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.model.Token;
import com.team3.personalfinanceapp.utils.UserApi;
import com.team3.personalfinanceapp.utils.APIClient;

import org.json.JSONException;
import org.json.JSONObject;

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
    SharedPreferences pref_rember;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor_rember;
    CheckBox checkBox;
    String username;
    String password;
    AccessToken accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        pref_rember = getSharedPreferences("remember_password", MODE_PRIVATE);

        if (pref_rember.contains("email") && pref_rember.contains("password")) {
            txtUsername.setText(pref_rember.getString("email", ""));
            txtPassword.setText(pref_rember.getString("password", ""));
            checkBox.setChecked(pref_rember.getBoolean("check", false));
        }


        if (pref.contains("token") && pref.contains("userid")) {
            checkToken(pref.getInt("userid", 0), pref.getString("token", ""));
        }
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            checkToken(pref.getInt("userid", 0), pref.getString("token", ""));
        }

        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> clickedLogin());


        signUp.setOnClickListener(v -> register());

        login();
        LoginWithFacebook.setOnClickListener(v -> LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile")));


    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);
        btnLogin.setEnabled(true);
        checkBox.setEnabled(true);
        error.setEnabled(true);
        signUp.setEnabled(true);
        LoginWithFacebook.setEnabled(true);
        txtForgetPassword.setEnabled(true);
    }


    private void init() {
        txtUsername = findViewById(R.id.txtLoginUsername);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        checkBox = findViewById(R.id.remember_checkbox);
        error = findViewById(R.id.error_msg);
        signUp = findViewById(R.id.txtSignUp);
        LoginWithFacebook = findViewById(R.id.LoginWithFacebook);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);

        apiInterface = APIClient.getClient().create(UserApi.class);
        callbackManager = CallbackManager.Factory.create();
    }

    private void checkToken(Integer uid, String token) {

        Call<Token> userLoginCall = apiInterface.checkToken(uid, "Bearer " + token);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.body() == null) {
                    Log.i("login", "error");
                    Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_SHORT).show();
                    editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    LoginManager.getInstance().logOut();
                    return;
                }
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }

    private void login() {

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

//                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                        startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.i("login", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.i("login", "onError");
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void clickedLogin() {

//        boolean chk = !checkBox.isChecked();
//        String c = String.valueOf(chk);
//        Log.i("check", c);
        username = txtUsername.getText().toString();
        password = txtPassword.getText().toString();
        if (txtPassword.getText().toString().isEmpty() && txtUsername.getText().toString().isEmpty()) {
            error.setText("Field cannot empty");
            return;
        }
        Call<Token> login = apiInterface.userLogin(username, password);
        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.body().access_token.equals("400")) {
                    error.setText("Check Your Password Or Email");
                    return;
                }
                txtUsername.setEnabled(false);
                txtPassword.setEnabled(false);
                btnLogin.setEnabled(false);
                checkBox.setEnabled(false);
                error.setEnabled(false);
                signUp.setEnabled(false);
                LoginWithFacebook.setEnabled(false);
                txtForgetPassword.setEnabled(false);
                pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("token", response.body().refresh_token);
                editor.putString("password", password);
                editor.commit();

                pref_rember = getSharedPreferences("remember_password", MODE_PRIVATE);
                editor_rember = pref_rember.edit();
                if (checkBox.isChecked()) {
                    editor_rember.putString("email", username);
                    editor_rember.putString("password", password);
                    editor_rember.putBoolean("check", true);
                    editor_rember.commit();
                } else {
                    editor_rember.clear();
                    editor_rember.commit();
                }

                fetchUserId(response.body().refresh_token, username);
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.i("fail", "fail");
            }
        });
    }

    private void fetchUserId(String token, String email) {

        Call<RegisteredUsers> userLoginCall = apiInterface.getUserInfo(email, "Bearer " + token);
        userLoginCall.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {

                if (response.body().getStatus().equals("PENDING")) {
//                    txtUsername = findViewById(R.id.txtLoginUsername);
//                    txtPassword = findViewById(R.id.txtLoginPassword);
//                    btnLogin = findViewById(R.id.btnLogin);
//                    checkBox = findViewById(R.id.remember_checkbox);
//                    error = findViewById(R.id.error_msg);
//                    signUp = findViewById(R.id.txtSignUp);
//                    LoginWithFacebook = findViewById(R.id.LoginWithFacebook);
//                    txtForgetPassword = findViewById(R.id.txtForgetPassword);
//                    txtUsername.setSaveEnabled(false);
//                    txtPassword.setSaveEnabled(false);
//                    btnLogin.setEnabled(false);
//                    checkBox.setEnabled(false);
//                    error.setEnabled(false);
//                    signUp.setEnabled(false);
//                    LoginWithFacebook.setEnabled(false);
//                    txtForgetPassword.setEnabled(false);

                    sendEmail(email);
                } else {
                    pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putInt("userid", response.body().getId());
                    editor.putString("username", response.body().getFullName());
                    editor.putString("email", response.body().getEmail());
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

    private void sendEmail(String email) {
        Call<Token> userLoginCall = apiInterface.sendOTPByEmail(email);
        userLoginCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Intent intent = new Intent(LoginActivity.this, VerifyAccountActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", response.body().access_token);
                intent.putExtra("register", true);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }


}