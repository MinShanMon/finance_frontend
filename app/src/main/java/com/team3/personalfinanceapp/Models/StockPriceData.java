package com.team3.personalfinanceapp.Models;

import java.util.List;

import com.team3.personalfinanceapp.Models.StockPrice;


public class StockPriceData {

   private List<StockPrice> values;

    public List<StockPrice> getData() {
        return values;
    }

    public void setData(List<StockPrice> values) {
        this.values = values;
    }

    public StockPriceData(List<StockPrice> values) {
        this.values = values;
    }

    public StockPriceData() {

    }


}
