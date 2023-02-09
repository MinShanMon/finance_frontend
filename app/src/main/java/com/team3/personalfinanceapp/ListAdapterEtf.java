package com.team3.personalfinanceapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.team3.personalfinanceapp.Models.Etf;
import com.team3.personalfinanceapp.Models.FixedDeposits;

import java.util.List;
import java.util.Locale;

public class ListAdapterEtf extends ArrayAdapter<Object> {

    private Context context;
    protected List<Etf> etfList;

    public ListAdapterEtf(Context context, List<Etf> etfList) {
        super(context, R.layout.rowetf);
        this.context=context;
        this.etfList = etfList;

        addAll(new Object[etfList.size()]);
    }

    @NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rowetf, parent, false);
        }

        String mname = etfList.get(pos).getName().toUpperCase(Locale.ROOT);
        String msymbol = etfList.get(pos).getSymbol().toUpperCase(Locale.ROOT);
        String mcurrency = etfList.get(pos).getCurrency().toUpperCase(Locale.ROOT);
        TextView name = view.findViewById(R.id.etfname);
        TextView symbol = view.findViewById(R.id.symbol);
        TextView currency = view.findViewById(R.id.currency);
        name.setText(mname);
        symbol.setText("symbol:   "+msymbol);
        currency.setText("currency: "+mcurrency);

        return view;
    }
}
