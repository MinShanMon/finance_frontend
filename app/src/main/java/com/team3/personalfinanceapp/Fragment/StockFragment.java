package com.team3.personalfinanceapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.team3.personalfinanceapp.ListAdapterEtf;
import com.team3.personalfinanceapp.ListAdapterStock;
import com.team3.personalfinanceapp.Models.Stock;
import com.team3.personalfinanceapp.Models.EtfData;
import com.team3.personalfinanceapp.Models.StockData;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.etfServics;
import com.team3.personalfinanceapp.config.APIclientstock;
import com.team3.personalfinanceapp.Services.stockServics;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment {

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stock, container, false);

        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        APIclientstock apIclientstock = new APIclientstock();

        stockServics ss = apIclientstock.getRetrofit().create(stockServics.class);
        Call<StockData> call = ss.getAll();

        call.enqueue(new Callback<StockData>() {
            @Override
            public void onResponse(Call<StockData> call, Response<StockData> response) {

                List<Stock> stockList = response.body().getData().stream().filter(e -> e.getCountry().compareTo("Singapore") == 0).collect(Collectors.toList());

                ListView listView = v.findViewById(R.id.listView);
                if (listView != null) {
                    listView.setAdapter(new ListAdapterStock(getContext(), stockList));
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                            Bundle bundle = new Bundle();
//                            bundle.putInt("deposists", position);
//
//                            BankDetailFragment bankDetailFragment = new BankDetailFragment();
//                            bankDetailFragment.setArguments(bundle);
//                            commitTransaction(bankDetailFragment);
//                        }
//                    });



                }
                Toast.makeText(getContext(),"successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<StockData> call, Throwable t) {
                Toast.makeText(getContext(),"failur",Toast.LENGTH_SHORT).show();
            }
        });

        return  v;


//
    }
}