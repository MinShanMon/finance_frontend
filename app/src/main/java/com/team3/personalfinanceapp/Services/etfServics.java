package com.team3.personalfinanceapp.Services;


import com.team3.personalfinanceapp.Models.EtfData;
import com.team3.personalfinanceapp.Models.StockPriceData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface etfServics {

    @GET("etf?apikey=de25cdffc19b4f669a62fdb8ef434e09")
    Call<EtfData> getAll();

    @GET
    Call<StockPriceData> getetfPrice(@Url String url);
}

