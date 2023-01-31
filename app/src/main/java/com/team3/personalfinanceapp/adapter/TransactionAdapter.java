package com.team3.personalfinanceapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Object> {
    private final Context context;

    protected ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, R.layout.transactions_listrow);
        this.context = context;
        this.transactions = transactions;

        if (transactions != null) {
            addAll(new Object[transactions.size()]);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.transactions_listrow, parent, false);
        }

        TextView transactionRecord = view.findViewById(R.id.transaction_item);
        Transaction currTransaction = transactions.get(position);
        String transStr = currTransaction.getTitle() + "\n"
                + currTransaction.getDate() + "\n"
                + currTransaction.getCategory() + "\n"
                + currTransaction.getAmount();

        transactionRecord.setText(transStr);
        return view;
    }
}
