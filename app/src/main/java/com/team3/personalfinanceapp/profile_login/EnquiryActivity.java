package com.team3.personalfinanceapp.profile_login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Enquiry;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.UserApi;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EnquiryActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    UserApi apiInterface;
    Intent intent;
    Boolean fill_enquiry;
    Enquiry enquiryObj;
    Integer id;
    String fullName;
    String email;
    String question;
    String enquiryType;


    ImageView img_backArrow;
    TextView enq_form;
    TextInputEditText name, mail, inquiry;
    Button sub;
    TextView error_msg;

    String[] items = {"Account", "Product", "Feedback", "Other"};
    AutoCompleteTextView type;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);

        init();

        img_backArrow.setOnClickListener(v -> finish());

        sub.setOnClickListener(v -> fillEnquiry());
    }

    private void init() {
        apiInterface = APIClient.getClient().create(UserApi.class);
        img_backArrow = findViewById(R.id.img_backArrow);
        enq_form = findViewById(R.id.enq_form);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        inquiry = findViewById(R.id.inquiry);
        sub = findViewById(R.id.submit);
        type = findViewById(R.id.type);

        adapterItems = new ArrayAdapter<>(this, R.layout.enquiry_item, items);
        type.setAdapter(adapterItems);
        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), " " + item, Toast.LENGTH_SHORT).show();
            }
        });

        intent = getIntent();
        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        fullName = pref.getString("username", "");
        if (!fullName.isEmpty()) {
            name.setText(fullName);
        }

        email = pref.getString("email", "");
        if (!email.isEmpty()) {
            mail.setText(email);
        }

    }

    private void fillEnquiry() {

        enquiryObj = new Enquiry();
        id = pref.getInt("userid", 0);
        fullName = pref.getString("username", "");
        email = mail.getText().toString();
        question = inquiry.getText().toString();
        enquiryType = type.getText().toString();

        enquiryObj.setFullName(fullName);
        enquiryObj.setEmail(email);
        enquiryObj.setEnquiryType(enquiryType);
        enquiryObj.setQuestion(question);

        if (!validateForm()) {
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mail.setText("please enter valid email");
            return;
        }

        Call<Boolean> createEnquiryCall = apiInterface.createEnquiry(enquiryObj, "Bearer " + pref.getString("token", ""));
        System.out.println(createEnquiryCall.request());
        createEnquiryCall.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                finish();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                call.cancel();
            }

        });
    }

    private boolean validateForm() {
        if (email.isEmpty() || fullName.isEmpty() || enquiryType.isEmpty() || question.isEmpty()) {

            TextView enquiryError = findViewById(R.id.enquiry_type_error);
            TextView fullNameError = findViewById(R.id.fullname_error);
            TextView emailError = findViewById(R.id.email_error);
            TextView questionError = findViewById(R.id.question_error);

            if (enquiryType.isEmpty()) {

                enquiryError.setVisibility(View.VISIBLE);
            } else {
                enquiryError.setVisibility(View.GONE);
            }


            if (fullName.isEmpty()) {

                fullNameError.setVisibility(View.VISIBLE);
            } else {
                fullNameError.setVisibility(View.GONE);
            }

            if (email.isEmpty()) {
                emailError.setVisibility(View.VISIBLE);
            } else {
                emailError.setVisibility(View.GONE);
            }

            if (question.isEmpty()) {
                questionError.setVisibility(View.VISIBLE);
            } else {
                questionError.setVisibility(View.GONE);
            }

            return false;
        }
        return true;
    }
}
