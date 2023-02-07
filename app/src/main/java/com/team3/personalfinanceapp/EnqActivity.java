package com.team3.personalfinanceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

//import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import retrofit2.Call;

public class EnqActivity extends AppCompatActivity {

    TextInputEditText userName,contactNum,contactMail,enquiry;
    Button subEnquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enquiry);

        userName=findViewById(R.id.name);
        contactNum=findViewById(R.id.email);
        contactMail=findViewById(R.id.phone);
        enquiry=findViewById(R.id.question);
        subEnquiry = findViewById(R.id.submit);

        subEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUser(createRequest());
            }
        });
    }

    public UserRequest createRequest(){
        UserRequest userRequest = new UserRequest();
        userRequest.setName(userName.getText().toString());
        userRequest.setEmail(contactMail.getText().toString());
        userRequest.setPhone(contactNum.getText().toString());
        userRequest.setQuestion(enquiry.getText().toString());

        return userRequest;
    }

    public void saveUser(UserRequest userRequest){
        Call<UserResponse> userResponseCall = APIClient.getUserService().saveUsers(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response){
                if(response.isSuccessful()){
                    Toast.makeText(EnqActivity.this,"Your enquiry has been submitted successfully!"
                            ,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EnqActivity.this,"Your enquiry has failed to submit.Try again."
                            ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t){
                Toast.makeText(EnqActivity.this,"Your enquiry has failed to submit.Try again."
                                +t.getLocalizedMessage()
                        ,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
