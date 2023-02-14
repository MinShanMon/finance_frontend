package com.team3.personalfinanceapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.fixedDeposistsServics;
import com.team3.personalfinanceapp.config.APIclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankDetailFragment extends Fragment {

    SharedPreferences pref;
    public BankDetailFragment() {
        // Required empty public constructor
    }
    private List<FixedDeposits> fixedList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank_detail, container, false);

        pref = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        int id = getArguments().getInt("deposists");



        getActivity().setTitle("fixed deposists detail");

        


        APIclient api = new APIclient();

        fixedDeposistsServics fs = api.getRetrofit().create(fixedDeposistsServics.class);
        Call<List<FixedDeposits>> call = fs.getAll("Bearer "+ pref.getString("token", ""));
        System.out.println(call.request());
        call.enqueue(new Callback<List<FixedDeposits>>() {

            @Override
            public void onResponse(Call<List<FixedDeposits>> call, Response<List<FixedDeposits>> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(getContext(),"unsuccessful",Toast.LENGTH_SHORT).show();
                    return;
                }

                fixedList= response.body();
                FixedDeposits item =fixedList.get(id);

                TextView bank = v.findViewById(R.id.bank);
                TextView mTenure = v.findViewById(R.id.tenure);
                TextView min = v.findViewById(R.id.minDeposits);
                TextView max = v.findViewById(R.id.maxDeposits);
                TextView interest = v.findViewById(R.id.interestRate);
                TextView date = v.findViewById(R.id.updateDate);

                mTenure.setText(Integer.toString(item.getTenure()) + " Months");
                bank.setText(item.getBank().getBankName().toUpperCase(Locale.ROOT));
                min.setText(Integer.toString(item.getMinAmount()) +" SGD");
                max.setText(Integer.toString(item.getMaxAmount()) +" SGD");
                interest.setText(Double.toString(item.getInterestRate()) );
                String[] dateDate  = item.getUpdateDate().split("-");
                date.setText(dateDate[0] + " - " + dateDate[1]);

                List<Long> fixedIdList = new ArrayList<Long>();

                Button addbtn =v.findViewById(R.id.add);
                Button compare = v.findViewById(R.id.compare);

                SharedPreferences pref = getActivity().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

//                Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
                Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();

                if(fixedIdMap.size() == 0){
                    compare.setText("0");
                }else{
                    compare.setText("compare "+Integer.toString(fixedIdMap.size()) + " items");
                }

                if(!pref.contains("fixedId-"+Long.toString(item.getId() -1 ))){
                    addbtn.setText("Add to Compare");
                }else {
                   addbtn.setText("Remove form Compare");
                }


                v.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BankFragment bankFragment = new BankFragment();
                        commitTransaction(bankFragment);
                    }
                });

                addbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
                        System.out.println(fixedIdMap.size());
                        if(fixedIdMap.size() == 2){
                            Toast.makeText(getContext(), "can not add anymore",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(fixedIdMap.containsKey("fixedId-"+Long.toString(item.getId() -1 ))){
                            editor.remove("fixedId-"+Long.toString(item.getId() -1));
                            editor.commit();

                        }else{
                            editor.putLong ("fixedId-"+Long.toString(item.getId() - 1),item.getId() -1);
                            editor.commit();
                        }

                        Map<String, Long> fixedIdMap2 = (Map<String, Long>) pref.getAll();
                        if(!fixedIdMap2.containsKey("fixedId-"+Long.toString(item.getId() -1 ))){
                            addbtn.setText("Add to Compare");
                        }else {
                            addbtn.setText("Remove form Compare");
                        }
                        compare.setText("compare "+Integer.toString(fixedIdMap2.size()) + " items");

                    }
                }
                );
                compare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();

                        if(fixedIdMap.size() == 1 || fixedIdMap.size() == 0){
                            Toast.makeText(getContext(), "You Need to choose two or more items to compare", Toast.LENGTH_LONG).show();
                            return;
                        }

                        CompareFragment compareFragment = new CompareFragment();
                        commitTransaction(compareFragment);

                    }
                });

                v.findViewById(R.id.link).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("link", item.getBank().getBankLink());

                        WebViewFragment webViewFragment = new WebViewFragment();
                        webViewFragment.setArguments(bundle);
                        commitTransaction(webViewFragment);
                    }
                });


            }

            @Override
            public void onFailure(Call<List<FixedDeposits>> call, Throwable t) {
                Toast.makeText(getContext(),"failure",Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().setTitle("fixed deposits detail");
        return v;
    }

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    private void commitTransaction(Fragment fragment) {
//        FragmentManager fm = getParentFragmentManager();
//        FragmentTransaction trans = fm.beginTransaction();
//        trans.replace(R.id.fragment_container, fragment);
//        trans.addToBackStack(null);
//        trans.commit();
//    }







}