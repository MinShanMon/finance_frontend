package com.team3.personalfinanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team3.personalfinanceapp.Models.FixedDeposits;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        String bankName = fixedList.get(pos).getBank().getBankName().toUpperCase(Locale.ROOT);
        String months = Integer.toString(fixedList.get(pos).getTenure());
        String interest = Double.toString(fixedList.get(pos).getInterestRate());
        TextView bank = view.findViewById(R.id.whichbank);
        TextView period = view.findViewById(R.id.period);
        TextView minterest = view.findViewById(R.id.whatinterest);
        bank.setText("    "+bankName);
        period.setText(months+"months");
        minterest.setText(interest);
        LinearLayout ll = view.findViewById(R.id.chooseview);

        final Boolean[] clicked = {false};


        SharedPreferences pref = getContext().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        view.findViewById(R.id.cbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
                int ppo =   Long.valueOf(fixedList.get(pos).getId()).intValue();



                if(fixedIdMap.size() == 2 && clicked[0]){


                    Drawable d = getContext().getDrawable(R.drawable.bg2);

                    ll.setBackground(d);

                    editor.remove("fixedId-"+Long.toString(ppo-1));
                    editor.commit();






                    clicked[0] = false;

                }else if (fixedIdMap.size() == 2 && !clicked[0]){

                    Toast.makeText(getContext(),"Can not choose anymore",Toast.LENGTH_SHORT).show();
                }else{

                    if(!clicked[0]){
                        Drawable d = getContext().getDrawable(R.drawable.bg1);
                        ll.setBackground(d);
                        editor.putLong("fixedId-"+Long.toString(ppo-1),ppo-1);
                        editor.commit();




                        clicked[0] = true;
                    }else{
                        Drawable d = getContext().getDrawable(R.drawable.bg2);
                        ll.setBackground(d);

                        editor.remove("fixedId-"+Long.toString(ppo-1));
                        editor.commit();


                        clicked[0] = false;
                    }

                }







            }
        });

//        SharedPreferences pref = getContext().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);
//        Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
//
//
//        if(fixedIdMap.containsKey("fixedId-"+Long.toString(pos))){
//
//            Drawable d = getContext().getDrawable(R.drawable.bg1);
//            ll.setBackground(d);
//        }

        return view;
    }



}
