package com.team3.personalfinanceapp.statements;

import androidx.appcompat.app.AppCompatActivity;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.BankStatementResponse;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.BankAPIInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatementsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);
        getAllStatementsAndDisplay();
    }

    private void getAllStatementsAndDisplay() {

        SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
        SharedPreferences bankPref = getSharedPreferences("user_banklist", MODE_PRIVATE);
        Set<String> bankAccountSet =
                bankPref.getStringSet(String.valueOf(pref.getInt("userid", 0)), new HashSet<>());

        if (bankAccountSet.isEmpty()) {
            return;
        }

        String[] bankAccts = bankAccountSet.toArray(new String[0]);

        String[] bankDetail = bankAccts[0].split(":");

        if (bankDetail.length == 0) {
            return;
        }

        BankAPIInterface bankAPIInterface = APIClient.getBankClient().create(BankAPIInterface.class);
        Call<BankStatementResponse> bankStatementResponseCall =
                bankAPIInterface.getStatementDetails(getString(R.string.ocbc_auth_header), bankDetail[1]);

        bankStatementResponseCall.enqueue(new Callback<BankStatementResponse>() {
            @Override
            public void onResponse(Call<BankStatementResponse> call, Response<BankStatementResponse> response) {
                BankStatementResponse bankStatementResponse = response.body();
                List<BankStatementResponse.ActivityDetails> activityDetailsList = bankStatementResponse.getResults().getActivityDetails();
                displayStatements(activityDetailsList);
            }

            @Override
            public void onFailure(Call<BankStatementResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void displayStatements(List<BankStatementResponse.ActivityDetails> activityDetailsList) {
        ListView listView = findViewById(R.id.bank_statement_listview);
        listView.setAdapter(new StatementListAdapter(this, activityDetailsList));
    }
}