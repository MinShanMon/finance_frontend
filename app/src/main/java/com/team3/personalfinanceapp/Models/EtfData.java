package com.team3.personalfinanceapp.Models;

import java.util.List;


public class EtfData {

   private List<Etf> data;

    public List<Etf> getData() {
        return data;
    }

    public void setData(List<Etf> data) {
        this.data = data;
    }

    public EtfData(List<Etf> data) {
        this.data = data;
    }

    public EtfData() {

    }


}
