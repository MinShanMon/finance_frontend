package com.team3.personalfinanceapp.Models;

import java.util.List;


public class StockData {

   private List<Stock> data;

    public List<Stock> getData() {
        return data;
    }

    public void setData(List<Stock> data) {
        this.data = data;
    }

    public StockData(List<Stock> data) {
        this.data = data;
    }

    public StockData() {

    }


}
