package com.team3.personalfinanceapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team3.personalfinanceapp.ListAdapter;
import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.fixedDeposistsServics;
import com.team3.personalfinanceapp.config.APIclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankFragment extends Fragment {

    public BankFragment() {
        // Required empty public constructor
    }
    private List<FixedDeposits> fixedList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank, container, false);


        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        APIclient api = new APIclient();

        fixedDeposistsServics fs = api.getRetrofit().create(fixedDeposistsServics.class);
        Call<List<FixedDeposits>> call = fs.getAll();

        call.enqueue(new Callback<List<FixedDeposits>>() {
            @Override
            public void onResponse(Call<List<FixedDeposits>> call, Response<List<FixedDeposits>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(),"unsuccressful",Toast.LENGTH_SHORT).show();
                    return;
                }


                fixedList= response.body();
                ListView listView = v.findViewById(R.id.listView);
                if (listView != null) {
                    listView.setAdapter(new ListAdapter(getContext(), fixedList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Bundle bundle = new Bundle();
                            bundle.putInt("deposists", position);

                            BankDetailFragment bankDetailFragment = new BankDetailFragment();
                            bankDetailFragment.setArguments(bundle);
                            commitTransaction(bankDetailFragment);
                        }
                    });



                }
            }

            @Override
            public void onFailure(Call<List<FixedDeposits>> call, Throwable t) {
                Toast.makeText(getContext(),"failure",Toast.LENGTH_SHORT).show();
            }
        });

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
//
//        ListView listView = v.findViewById(R.id.listView);
//        if (listView != null) {
//        listView.setAdapter(new ListAdapter(getActivity(), fixedList));
//
//        }