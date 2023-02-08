package com.team3.personalfinanceapp.statements;

import androidx.appcompat.app.AppCompatActivity;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.BankStatementResponse;
import com.team3.personalfinanceapp.utils.APIClient;
import com.team3.personalfinanceapp.utils.BankAPIInterface;

import android.os.Bundle;

import java.util.List;

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

        BankAPIInterface bankAPIInterface = APIClient.getBankClient().create(BankAPIInterface.class);
        Call<BankStatementResponse> bankStatementResponseCall =
                bankAPIInterface.getStatementDetails(getString(R.string.ocbc_auth_header));

        bankStatementResponseCall.enqueue(new Callback<BankStatementResponse>() {
            @Override
            public void onResponse(Call<BankStatementResponse> call, Response<BankStatementResponse> response) {
                BankStatementResponse bankStatementResponse = response.body();
                List<BankStatementResponse.ActivityDetails> activityDetailsList = bankStatementResponse.getResults().getActivityDetails();
//                activityDetailsList.forEach(e -> {
//
//                        }
//                );

            }

            @Override
            public void onFailure(Call<BankStatementResponse> call, Throwable t) {
                call.cancel();
            }
        });


    }
}