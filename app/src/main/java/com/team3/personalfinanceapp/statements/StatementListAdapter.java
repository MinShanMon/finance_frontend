package com.team3.personalfinanceapp.statements;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.BankStatementResponse;

import java.util.List;
import java.util.Locale;

public class StatementListAdapter extends ArrayAdapter<Object> {

    private List<BankStatementResponse.ActivityDetails> activityDetailsList;
    private Context context;

    public StatementListAdapter(@NonNull Context context, List<BankStatementResponse.ActivityDetails> activityDetailsList) {
        super(context, R.layout.row_statement);
        this.context = context;
        this.activityDetailsList = activityDetailsList;
        addAll(new Object[activityDetailsList.size()]);
    }

    @NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_statement, parent, false);
        }
        BankStatementResponse.ActivityDetails statement = activityDetailsList.get(pos);
        TextView date = view.findViewById(R.id.statement_date);
        date.setText(statement.getDate().getMonth().toString() + " " + statement.getDate().getYear());

        TextView amount = view.findViewById(R.id.statement_amount);
        amount.setText("$" + String.format(context.getResources().getString(R.string.money_format), statement.getAverageBalance()));
        return view;
    }


}
