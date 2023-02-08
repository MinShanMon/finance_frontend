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

import com.team3.personalfinanceapp.ListAdapter;
import com.team3.personalfinanceapp.ListAdapterEtf;
import com.team3.personalfinanceapp.Models.Etf;
import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.Models.Stock;
import com.team3.personalfinanceapp.Models.EtfData;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.fixedDeposistsServics;
import com.team3.personalfinanceapp.config.APIclientetf;
import com.team3.personalfinanceapp.config.APIclientstock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.team3.personalfinanceapp.Services.etfServics;

public class EtfFragment extends Fragment {

    public EtfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_etf, container, false);

        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        List<String> validEtfs = Arrays.asList("SPY","SPCZ");
//
          APIclientetf apIclientetf = new APIclientetf();
//

        etfServics es = apIclientetf.getRetrofit().create(etfServics.class);
        Call<EtfData> call = es.getAll();

        call.enqueue(new Callback<EtfData>() {
            @Override
            public void onResponse(Call<EtfData> call, Response<EtfData> response) {



                List<Etf> etfList = response.body().getData().stream().filter(e -> validEtfs.contains(e.getSymbol())).collect(Collectors.toList());

                ListView listView = v.findViewById(R.id.listView);
                if (listView != null) {
                    listView.setAdapter(new ListAdapterEtf(getContext(), etfList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Bundle bundle = new Bundle();
                            bundle.putString("symbol", etfList.get(position).getSymbol());




                            EtfDetailFragment etfDetailFragment = new EtfDetailFragment();
                            etfDetailFragment.setArguments(bundle);
                            commitTransaction(etfDetailFragment);
                        }
                    });



                }

                Toast.makeText(getContext(),"successful",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<EtfData> call, Throwable t) {
                Toast.makeText(getContext(),"failer",Toast.LENGTH_SHORT).show();
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