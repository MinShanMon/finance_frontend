package com.team3.personalfinanceapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.team3.personalfinanceapp.Models.StockPrice;
import com.team3.personalfinanceapp.Models.StockPriceData;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.etfServics;
import com.team3.personalfinanceapp.config.APIclientstock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EtfDetailFragment extends Fragment {



    public EtfDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_etf_detail, container, false);

        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        String symbol = getArguments().getString("symbol");

        APIclientstock apIclientstock = new APIclientstock();

//        https://api.twelvedata.com/time_series?symbol=SPCZ&interval=1min&apikey=de25cdffc19b4f669a62fdb8ef434e09
        etfServics ed = apIclientstock.getRetrofit().create(etfServics.class);
        Call<StockPriceData> call = ed.getetfPrice("time_series?symbol="+symbol+"&interval=1min&apikey=de25cdffc19b4f669a62fdb8ef434e09");


        call.enqueue(new Callback<StockPriceData>() {
            @Override
            public void onResponse(Call<StockPriceData> call, Response<StockPriceData> response) {


                StockPrice priceList = response.body().getData().get(0);

                TextView update = v.findViewById(R.id.update);
                TextView open = v.findViewById(R.id.open);
                TextView high = v.findViewById(R.id.high);
                TextView low = v.findViewById(R.id.low);
                TextView close = v.findViewById(R.id.close);
                TextView volume = v.findViewById(R.id.volume);

                update.setText(priceList.getDatetime());
                open.setText(priceList.getOpen() + "%");
                high.setText(priceList.getHigh() + "%");
                low.setText(priceList.getLow() + "%");
                close.setText(priceList.getClose() + "%");
                volume.setText(priceList.getVolume() + "%");




                v.findViewById(R.id.link).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("symbol", symbol);

                        EtfWebViewFragment webViewFragment = new EtfWebViewFragment();
                        webViewFragment.setArguments(bundle);
                        commitTransaction(webViewFragment);
                    }
                });

                v.findViewById(R.id.backk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EtfFragment etfFragment = new EtfFragment();
                        commitTransaction(etfFragment);
                    }
                });

            }



            @Override
            public void onFailure(Call<StockPriceData> call, Throwable t) {
                Toast.makeText(getContext(),"fail",Toast.LENGTH_SHORT).show();
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