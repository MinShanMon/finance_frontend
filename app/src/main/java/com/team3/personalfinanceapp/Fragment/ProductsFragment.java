package com.team3.personalfinanceapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.insights.InsightsViewPagerFragment;


public class ProductsFragment extends Fragment {

    private IProductFragment iProductFragment;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_products, container, false);


        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));


        v.findViewById(R.id.bank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getActivity().getSharedPreferences("bankIdList", getContext().MODE_PRIVATE);
                pref.edit().clear().commit();

                BankFragment bankFragment = new BankFragment();
                commitTransaction(bankFragment);

            }
        });

        v.findViewById(R.id.etf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EtfFragment etfFragment = new EtfFragment();
                commitTransaction(etfFragment);

            }
        });

        v.findViewById(R.id.stock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StockFragment stockFragment = new StockFragment();
                commitTransaction(stockFragment);

            }
        });

        return  v;
    }

    private void commitTransaction(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.fragment_container, fragment);
        trans.addToBackStack(null);
        trans.commit();
    }

    @Override
    public void onStart(){
        super.onStart();
        viewDetailProduct(R.id.products_item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iProductFragment = (IProductFragment) context;
    }

    void viewDetailProduct(int itemId) {
        iProductFragment.viewDetailProduct(itemId);
    }
    public interface IProductFragment {
        void viewDetailProduct(int itemId);
    }
}