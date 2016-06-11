package com.swan.coolweather.model;

/**
 * Created by EDEH7311 on 06/11/2016.
 */
public class County extends Region {
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
