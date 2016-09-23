package com.example.phelps.simpleforecast.Data;

/**
 * Created by Phelps on 2016/8/22.
 */
public class CityEvent {
    public String cityName;

    public CityEvent(String string) {
        cityName = string;
    }

    public String getCityName() {
        return cityName;
    }
}
