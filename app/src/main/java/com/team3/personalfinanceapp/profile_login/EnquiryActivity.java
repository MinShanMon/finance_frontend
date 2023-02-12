//package com.team3.personalfinanceapp.profile_login;
//
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.material.textfield.TextInputEditText;
//import com.team3.personalfinanceapp.R;
//import com.team3.personalfinanceapp.model.Enquiry;
//import com.team3.personalfinanceapp.utils.APIClient;
//import com.team3.personalfinanceapp.utils.UserApi;
//
//import java.time.LocalDateTime;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
//public class EnquiryActivity extends AppCompatActivity {
//    SharedPreferences.Editor editor;
//    SharedPreferences pref;
//    UserApi apiInterface;
//    Intent intent;
//    Boolean fill_enquiry;
//    Enquiry enquiryObj;
//    Integer id;
//    String fullName;
//    String email;
//    String question;
//    String enquiryType;
//
//    // how to generate local dateTime when submit the enquiry?
//    LocalDateTime enquiry_dateTime;
//
//
//    ImageView img_backArrow;
//    TextView enq_form;
//    TextInputEditText name, mail, inquiry;
//    Button sub;
//    TextView error_msg;
//
//    String[] items = {"Account", "Product", "Feedback", "Other"};
//    AutoCompleteTextView type;
//    ArrayAdapter<String> adapterItems;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_enquiry);
//
//        init();
//
//
//        img_backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        sub.setOnClickListener(v -> {
//            if(fill_enquiry) {
//                fillEnquiry();
//            }
//        });
//
//    }
//
//    private void init() {
//        apiInterface = APIClient.getClient().create(UserApi.class);
//        img_backArrow = findViewById(R.id.img_backArrow);
//        enq_form = findViewById(R.id.enq_form);
//        name = findViewById(R.id.name);
//        mail = findViewById(R.id.mail);
//        inquiry = findViewById(R.id.inquiry);
//        sub = findViewById(R.id.submit);
//        type = findViewById(R.id.type);
//
//        adapterItems = new ArrayAdapter<String>(this, R.layout.enquiry_item, items);
//        type.setAdapter(adapterItems);
//        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String item = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), " " + item, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        intent = getIntent();
//        fill_enquiry = intent.getBooleanExtra("enquiry", false);
//        fullName = intent.getStringExtra("username");
//        email = intent.getStringExtra("email");
//    }
//
//    private void fillEnquiry(){
//
//        enquiryObj = new Enquiry();
//        pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
//        id = pref.getInt("userid", 0);
//        fullName= pref.getString("username","");
//        email = pref.getString("email","");
//        question = inquiry.getText().toString();
//        enquiryType = type.getText().toString();
//
//        enquiryObj.setFullName(fullName);
//        enquiryObj.setEmail(email);
//        enquiryObj.setEnquiryType(enquiryType);
//        enquiryObj.setQuestion(question);
//
//        if(email.isEmpty() && fullName.isEmpty() && enquiryType.isEmpty() && question.isEmpty()){
//            error_msg.setText("Field cannot be empty");
//            return;
//        }
//        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//
//            error_msg.setText("please enter valid email");
//            return;
//        }
//
//        Call<Enquiry> createEnquiryCall = apiInterface.createEnquiry(enquiryObj);
//
//        createEnquiryCall.enqueue(new Callback<Enquiry>() {
//            @Override
//            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
//
//                Intent intent = new Intent(EnquiryActivity.this, LoginActivity.class);
//                error_msg.setText("");
//                editor = pref.edit();
//                editor.clear();
//                editor.commit();
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<Enquiry> call, Throwable t) {
//
//            }
//
//        });
//    }
//
//}
