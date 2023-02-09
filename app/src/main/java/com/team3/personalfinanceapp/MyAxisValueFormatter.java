package com.team3.personalfinanceapp;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyAxisValueFormatter extends ValueFormatter {
    private DecimalFormat mFormat;

    public MyAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.000");
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return mFormat.format(value) + " $";
    }
}

