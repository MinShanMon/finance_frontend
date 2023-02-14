package com.team3.personalfinanceapp.profile_login;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.share.Share;
import com.google.gson.Gson;
import com.team3.personalfinanceapp.MainActivity;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.RegisteredUsers;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.UserApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {

    UserApi apiInterface;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        apiInterface = APIClient.getClient().create(UserApi.class);
        accessToken = AccessToken.getCurrentAccessToken();
        loginfb();
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
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<RegisteredUsers> call, Throwable t) {
                call.cancel();
            }
        });
    }
}