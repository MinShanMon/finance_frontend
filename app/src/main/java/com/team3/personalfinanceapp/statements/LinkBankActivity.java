package com.team3.personalfinanceapp.statements;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.team3.personalfinanceapp.R;

import java.util.HashSet;
import java.util.Set;

public class LinkBankActivity extends AppCompatActivity {

    private String[] banks;
    private String userId;

    private AutoCompleteTextView bankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_bank);


        banks = getResources().getStringArray(R.array.bank_list);
        bankList = findViewById(R.id.bank_dropdown);
        bankList.setAdapter(new ArrayAdapter<>(this, R.layout.enquiry_item, banks));

        ImageView backArrow = findViewById(R.id.img_backArrow);
        backArrow.setOnClickListener( c -> finish());

        Button submitBtn = findViewById(R.id.submit);
        submitBtn.setOnClickListener( c -> {
            if (!submitBank()) {
                Toast.makeText(this, "You have already linked this bank account", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
        });

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        userId = String.valueOf(pref.getInt("userid", 0));
    }

    private boolean submitBank() {

        SharedPreferences bankPref = getSharedPreferences("user_banklist", MODE_PRIVATE);
        Set<String> userBankList = new HashSet<>();
        SharedPreferences.Editor editor = bankPref.edit();

        String bankName = bankList.getText().toString();
        TextView accountNoField = findViewById(R.id.bank_account_num);
        String accountNo = accountNoField.getText().toString();
        String bankAccountNo = bankName + ":" + accountNo;
        if (!userBankList.add(bankAccountNo)) {
            return false;
        }
        editor.putStringSet(userId, userBankList);
        editor.commit();
        return true;
    }






}