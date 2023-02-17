package com.team3.personalfinanceapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.MyAxisValueFormatter;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.fixedDeposistsServics;
import com.team3.personalfinanceapp.XAxisValueFormatter;
import com.team3.personalfinanceapp.config.APIclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastPortfolioFragment extends Fragment{
    SharedPreferences prefs;
    public ForecastPortfolioFragment() {
    }
    private LineChart lineChart;
    private List<FixedDeposits> fixedDepositsList = new ArrayList<FixedDeposits>();
    List<Integer> amounts = new ArrayList<>();
    List<Integer> periods = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast_portfolio, container, false);
        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));
        prefs = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);

        SharedPreferences pref = getActivity().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);
        Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
        Collection<Long> Ids = fixedIdMap.values();
        List<Long> IdList = new ArrayList<Long>();
        IdList.addAll(Ids);
        EditText amount1 = v.findViewById(R.id.amount1);
        TextView period1 = v.findViewById(R.id.period1);
        EditText amount2 = v.findViewById(R.id.amount2);
        TextView period2 = v.findViewById(R.id.period2);

        APIclient api = new APIclient();
        fixedDeposistsServics fs = api.getRetrofit().create(fixedDeposistsServics.class);
        for (Long id : IdList) {
            if (id != null) {
                Call<FixedDeposits> call = fs.getById(id+1, "Bearer "+ prefs.getString("token", ""));
                call.enqueue(new Callback<FixedDeposits>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<FixedDeposits> call, Response<FixedDeposits> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(), "unsuccessful", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FixedDeposits result = response.body();
                        fixedDepositsList.add(result);
                        if (fixedDepositsList.size()==1) {
                            amount1.setText(Integer.toString(result.getMinAmount()));
                            period1.setText(Integer.toString(result.getTenure()));
                            amounts.add(Integer.parseInt (amount1.getText().toString()));
                            periods.add(Integer.parseInt (period1.getText().toString()));
                            System.out.println("p1:"+periods.get(0));
                        }
                        if (fixedDepositsList.size()==2) {
                            amount2.setText(Integer.toString(result.getMinAmount()));
                            period2.setText(Integer.toString(result.getTenure()));
                            amounts.add(Integer.parseInt (amount2.getText().toString()));
                            periods.add(Integer.parseInt (period2.getText().toString()));
                            System.out.println("p2:"+periods.get(1));
                            initLineChart(amounts,periods);
                        }
                    }
                    @Override
                    public void onFailure(Call<FixedDeposits> call, Throwable t) {
                        Toast.makeText(getContext(), "unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

        v.findViewById(R.id.backfromport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompareFragment compareFragment = new CompareFragment();
                commitTransaction(compareFragment);
            }
        });
        setupOnBackPressed();
        return v;
    }

    private void setupOnBackPressed(){
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CompareFragment compareFragment = new CompareFragment();
                commitTransaction(compareFragment);
            }
        });
    }

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }



    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState)   {
        super.onViewCreated(v, savedInstanceState);
        lineChart = v.findViewById(R.id.lineChart);
        ImageButton addAmountBtn1=v.findViewById(R.id.add_amount1);
        ImageButton addAmountBtn2=v.findViewById(R.id.add_amount2);
        ImageButton reduceAmountBtn1=v.findViewById(R.id.reduce_amount1);
        ImageButton reduceAmountBtn2=v.findViewById(R.id.reduce_amount2);
        EditText amount1 = v.findViewById(R.id.amount1);
        EditText amount2 = v.findViewById(R.id.amount2);
        amount1.addTextChangedListener(new TextWatcher() {
                private CharSequence word;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    word = s;
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (amounts.size() > 0) {
                        try {
                            if(word.length()==0||Integer.parseInt(word.toString())<fixedDepositsList.get(0).getMinAmount()) {
                                Toast.makeText(getContext(), "Can not less than" + fixedDepositsList.get(0).getMinAmount(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(Integer.parseInt(word.toString())>fixedDepositsList.get(0).getMaxAmount()) {

                                Toast.makeText(getContext(), "Can not more than" + fixedDepositsList.get(0).getMaxAmount(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            amounts.set(0, Integer.parseInt(amount1.getText().toString()));
                            setLineChartData(amounts, periods);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        amount2.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                word = s;
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (amounts.size() > 1) {
                    try {
                        if(word.length()==0||Integer.parseInt(word.toString())<fixedDepositsList.get(1).getMinAmount()) {
                            Toast.makeText(getContext(), "Amount can not less than" + fixedDepositsList.get(1).getMinAmount(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(Integer.parseInt(word.toString())>fixedDepositsList.get(1).getMaxAmount()) {

                            Toast.makeText(getContext(), "Amount can not more than" + fixedDepositsList.get(1).getMaxAmount(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        amounts.set(1, Integer.parseInt(amount2.getText().toString()));
                        setLineChartData(amounts, periods);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Invalid input!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button saveBtn = v.findViewById(R.id.save);

        reduceAmountBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a1=Integer.parseInt (amount1.getText().toString());
                if (a1>fixedDepositsList.get(0).getMinAmount()){
                    amount1.setText(Integer.toString(a1-1));
                    amounts.set(0,Integer.parseInt (amount1.getText().toString()));
                    setLineChartData(amounts,periods);
                }
            }
        });
        addAmountBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a1=Integer.parseInt (amount1.getText().toString());
                if (a1<fixedDepositsList.get(0).getMaxAmount()){
                    System.out.println(a1);
                    amount1.setText(Integer.toString(a1+1));
                    amounts.set(0,Integer.parseInt (amount1.getText().toString()));
                    setLineChartData(amounts,periods);
                }
            }
        });
        reduceAmountBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a2=Integer.parseInt (amount2.getText().toString());
                if (a2>fixedDepositsList.get(1).getMinAmount()){
                    amount2.setText(Integer.toString(a2-1));
                    amounts.set(1,Integer.parseInt (amount2.getText().toString()));
                    setLineChartData(amounts,periods);
                }
            }
        });
        addAmountBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a2=Integer.parseInt (amount2.getText().toString());
                if (a2<fixedDepositsList.get(1).getMaxAmount()){
                    System.out.println(a2);
                    amount2.setText(Integer.toString(a2+1));
                    amounts.set(1,Integer.parseInt (amount2.getText().toString()));
                    setLineChartData(amounts,periods);
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = UUID.randomUUID().toString();
                lineChart.saveToGallery(filename,50);
                Toast.makeText(getContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initLineChart(List<Integer> amounts,List<Integer> periods) {
//        lineChart.setOnChartValueSelectedListener(this);
        lineChart.getDescription().setEnabled(false);
        lineChart.setBackgroundColor(Color.WHITE);

        ValueFormatter xAxisFormatter = new XAxisValueFormatter();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(null);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setDrawLabels(true);

        ValueFormatter custom = new MyAxisValueFormatter();
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(null);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(-5f);

//        Description description = new Description();
//        description.setText("Month");
//        lineChart.setDescription(description);

        lineChart.getAxisRight().setEnabled(false);

        setLineChartData(amounts,periods);
    }
    private void setLineChartData(List<Integer> amounts,List<Integer> periods) {

        List<Entry> valsProd1 = new ArrayList<>();
        List<Entry> valsProd2 = new ArrayList<>();
        List<Entry> total = new ArrayList<>();
        Integer max = Collections.max(periods);
        for(int i=0;i<=max;i++){
            if(i<periods.get(0)){
                valsProd1.add(new Entry(i,-amounts.get(0)));
            }
            else
                valsProd1.add(new Entry(i, (float) (amounts.get(0)*(1+fixedDepositsList.get(0).getInterestRate()))));
        }
        for(int i=0;i<=max;i++){
            if(i<periods.get(1)){
                valsProd2.add(new Entry(i,-amounts.get(1)));
            }
            else
                valsProd2.add(new Entry(i, (float) (amounts.get(1)*(1+fixedDepositsList.get(1).getInterestRate()))));
        }


        for(int i=0;i<=max;i++){
            total.add(new Entry(i, valsProd1.get(i).getY()+valsProd2.get(i).getY()));
        }


        LineDataSet setProd1 = new LineDataSet(valsProd1, "Product A ");
        setProd1.setColor(Color.parseColor("#CC99FF"));
        setProd1.setCircleColor(Color.parseColor("#CC99FF"));
        setProd1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setProd1.setLineWidth(2f);
//        setProd1.setColor(getResources().getColor(R.color.purple_200));
        setProd1.setDrawCircles(true);

//        setProd1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineDataSet setProd2 = new LineDataSet(valsProd2, "Product B ");
        setProd2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setProd2.setDrawCircles(true);
        setProd2.setColor(Color.parseColor("#FFD700"));
        setProd2.setCircleColor(Color.parseColor("#FFD700"));
        setProd2.setLineWidth(2f);
//        setComp2.setColor(getResources().getColor(R.color.teal_200,));
        //smooth line
//        setProd2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineDataSet setTotal = new LineDataSet(total, "Total ");
        setTotal.setColor(Color.parseColor("#6699FF"));
        setTotal.setCircleColor(Color.parseColor("#6699FF"));
        setTotal.setLineWidth(2f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setProd1);
        dataSets.add(setProd2);
        dataSets.add(setTotal);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        lineChart.invalidate();
    }
}