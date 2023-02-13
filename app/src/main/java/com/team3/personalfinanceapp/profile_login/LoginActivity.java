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
                    Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_SHORT).show();
                    editor = pref.edit();
                    editor.clear();
                    editor.commit();
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
                        Log.i("here", "fb");
//                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        loginfb();
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


    private void loginfb() {
        accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code 1319406795267973
                        Gson gson = new Gson();
                        String json = gson.toJson(response);
                        Log.i("response", json);

                        try {
                            String fullname = object.getString("name");
                            String fbid = object.getString("id");
//                            SharedPreferences pref = getPreferences()
//                            //setting profile picture
//                            String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
//                            Picasso.get().load(url).into(imageView);
//                            txt.setText(fullname);
                            RegisteredUsers user = new RegisteredUsers();
                            user.setFbid(fbid);
                            user.setFullName(fullname);
                            register(user);
                            Log.i("response", fullname);
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

    private void register(RegisteredUsers ruser) {

        RegisteredUsers user = new RegisteredUsers();

        user.setFullName(ruser.getFullName());
        user.setFbid(ruser.getFbid());

        Call<RegisteredUsers> userLoginCall = apiInterface.regFbUser(user);
        userLoginCall.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {

                pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                editor = pref.edit();
                editor.putInt("userid", response.body().getId());
                editor.putString("username", response.body().getFullName());
                editor.putString("token", response.body().getJwtToken());
                editor.putString("email", response.body().getEmail());
                editor.putBoolean("fbuser", true);
                editor.commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<RegisteredUsers> call, Throwable t) {
                call.cancel();
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
                finish();
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }


}