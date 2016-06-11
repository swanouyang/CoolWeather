package com.swan.coolweather.model;

/**
 * Created by EDEH7311 on 06/11/2016.
 */
public class City extends Region {
    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    private int provinceId;

}
