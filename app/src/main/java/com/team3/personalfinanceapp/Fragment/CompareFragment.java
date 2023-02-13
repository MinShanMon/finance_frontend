package com.team3.personalfinanceapp.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.facebook.share.Share;
import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.fixedDeposistsServics;
import com.team3.personalfinanceapp.config.APIclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompareFragment extends Fragment {
    SharedPreferences pref;
    SharedPreferences prefs;
    public  CompareFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_compare, container, false);

        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));
        Button forecastBtn = v.findViewById(R.id.compare_next);
//        int id = getArguments().getInt("deposists");
        getActivity().setTitle("compare products");

        pref = getActivity().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);

        Map<String, Long> fixedIdMap = (Map<String, Long>) pref.getAll();
        Collection<Long> Ids = fixedIdMap.values();

        List<Long>IdList = new ArrayList<Long>();
        IdList.addAll(Ids);
        List<FixedDeposits> fixedDepositsList =new ArrayList<FixedDeposits>();
        APIclient api = new APIclient();
        fixedDeposistsServics fs = api.getRetrofit().create(fixedDeposistsServics.class);

        for (Long id:IdList) {

            if (id!=null){
                prefs = this.getActivity().getSharedPreferences("user_credentials", MODE_PRIVATE);
                Call<FixedDeposits> call = fs.getById(id, "Bearer "+ prefs.getString("token", ""));
//                System.out.println(call.request());

                call.enqueue(new Callback<FixedDeposits>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<FixedDeposits> call, Response<FixedDeposits> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(getContext(),"unsuccessful",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FixedDeposits result = response.body();
                        fixedDepositsList.add(result);

                        if (fixedDepositsList.size()==1) {
                            TextView productId1 = v.findViewById(R.id.product1_id);
                            TextView bank1 = v.findViewById(R.id.product1_bank);
                            TextView tenure1 = v.findViewById(R.id.product1_tenure);
                            TextView min1 = v.findViewById(R.id.product1_min);
                            TextView max1 = v.findViewById(R.id.product1_max);
                            TextView interest1 = v.findViewById(R.id.product1_rate);
                            TextView date1 = v.findViewById(R.id.product1_update);

                            productId1.setText(Long.toString(result.getId()));
                            tenure1.setText(Integer.toString(result.getTenure()));
                            bank1.setText(result.getBank().getBankName());
                            min1.setText(Integer.toString(result.getMinAmount()));
                            max1.setText(Integer.toString(result.getMaxAmount()));
                            interest1.setText(Double.toString(result.getInterestRate()));
//                            date1.setText(result.getUpdateDate());
                            String[] dateDate  = result.getUpdateDate().split("-");
                            date1.setText(dateDate[0] + " - " + dateDate[1]);

                        }
                        else if (fixedDepositsList.size()==2) {
                            TextView productId2 = v.findViewById(R.id.product2_id);
                            TextView bank2 = v.findViewById(R.id.product2_bank);
                            TextView tenure2 = v.findViewById(R.id.product2_tenure);
                            TextView min2 = v.findViewById(R.id.product2_min);
                            TextView max2 = v.findViewById(R.id.product2_max);
                            TextView interest2 = v.findViewById(R.id.product2_rate);
                            TextView date2 = v.findViewById(R.id.product2_update);

                            productId2.setText(Long.toString(result.getId()));
                            tenure2.setText(Integer.toString(result.getTenure()));
                            bank2.setText(result.getBank().getBankName());
                            min2.setText(Integer.toString(result.getMinAmount()));
                            max2.setText(Integer.toString(result.getMaxAmount()));
                            interest2.setText(Double.toString(result.getInterestRate()));
//                            date2.setText(result.getUpdateDate());
                            String[] dateDate  = result.getUpdateDate().split("-");
                            date2.setText(dateDate[0] + " - " + dateDate[1]);
                        }
                    }

                    @Override
                    public void onFailure(Call<FixedDeposits> call, Throwable t) {
                        Toast.makeText(getContext(),"unsuccessful",Toast.LENGTH_SHORT).show();
                    }
                });
            }

//            else
//                Toast.makeText(getContext(),"unsuccessful",Toast.LENGTH_SHORT).show();


//                Response<FixedDeposits> fixedDepositsResponse = call.execute();
            forecastBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ForecastPortfolioFragment forecastPortfolioFragment=new ForecastPortfolioFragment();
                    commitTransaction(forecastPortfolioFragment);
                }
            });
        }
        return v;
    }

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }
}
