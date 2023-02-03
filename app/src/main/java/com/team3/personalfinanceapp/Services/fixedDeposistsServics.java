package com.team3.personalfinanceapp.Services;



import com.team3.personalfinanceapp.Models.FixedDeposits;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface fixedDeposistsServics {

    @GET("fixeds")
    Call<List<FixedDeposits>> getAll();
}
