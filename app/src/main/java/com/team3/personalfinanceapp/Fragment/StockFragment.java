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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment {

    public StockFragment() {
        // Required empty public constructor
    }


    List<String> validSotck = Arrays.asList("D05","SVI","511880","BOTHE","INFY","005930","GMEXICOB","TKMIT","EBS");
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

                List<Stock> stockList = response.body().getData().stream().filter(e -> validSotck.contains(e.getSymbol())).collect(Collectors.toList());

                ListView listView = v.findViewById(R.id.listView);
                if (listView != null) {
                    listView.setAdapter(new ListAdapterStock(getContext(), stockList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Bundle bundle = new Bundle();
                            bundle.putString("symbol", stockList.get(position).getSymbol());
                            bundle.putString("exchange", stockList.get(position).getExchange());

                            StockDetailFragment stockDetailFragment = new StockDetailFragment();
                            stockDetailFragment.setArguments(bundle);
                            commitTransaction(stockDetailFragment);
                        }
                    });



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
    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }
}