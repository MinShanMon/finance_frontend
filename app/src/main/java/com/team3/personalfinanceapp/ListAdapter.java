package com.team3.personalfinanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team3.personalfinanceapp.Models.FixedDeposits;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Object> {

    private Context context;
    protected List<FixedDeposits> fixedList;

    public ListAdapter(Context context, List<FixedDeposits> fixedList) {
        super(context, R.layout.row);
        this.context=context;
        this.fixedList = fixedList;

        addAll(new Object[fixedList.size()]);
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, parent, false);
        }

        String bankName = fixedList.get(pos).getBank().getBankName();
        String months = Integer.toString(fixedList.get(pos).getTenure());
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(bankName + "  " + months);

        return view;
    }
}
