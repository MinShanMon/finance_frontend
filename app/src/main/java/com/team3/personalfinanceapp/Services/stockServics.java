package com.team3.personalfinanceapp.Services;


import com.team3.personalfinanceapp.Models.FixedDeposits;
import com.team3.personalfinanceapp.Models.Stock;
import com.team3.personalfinanceapp.Models.StockData;
import com.team3.personalfinanceapp.Models.StockPriceData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface stockServics {

    @GET("stocks?apikey=de25cdffc19b4f669a62fdb8ef434e09")
    Call<StockData> getAll();

    @GET
    Call<StockPriceData> getStockPrice(@Url String url);
}
