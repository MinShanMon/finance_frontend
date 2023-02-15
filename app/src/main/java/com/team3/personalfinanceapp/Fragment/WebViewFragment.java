package com.team3.personalfinanceapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import com.team3.personalfinanceapp.ListAdapterStock;
import com.team3.personalfinanceapp.Models.Stock;
import com.team3.personalfinanceapp.Models.StockData;
import com.team3.personalfinanceapp.R;
import com.team3.personalfinanceapp.Services.stockServics;
import com.team3.personalfinanceapp.config.APIclientstock;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewFragment extends Fragment {

    public WebViewFragment() {
        // Required empty public constructor
    }
    private String url;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);

        TransitionInflater tInflater = TransitionInflater.from(requireContext());
        setEnterTransition(tInflater.inflateTransition(R.transition.slide_right));
        String url = getArguments().getString("link");

        webView = v.findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return  v;
    }
}