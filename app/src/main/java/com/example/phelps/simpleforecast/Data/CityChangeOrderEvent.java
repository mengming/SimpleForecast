package com.example.phelps.simpleforecast.Data;

import com.example.phelps.simpleforecast.Fragment.CityFragment;

import java.util.List;

/**
 * Created by Phelps on 2016/9/26.
 */

public class CityChangeOrderEvent {

    private List<String> cityList;

    public CityChangeOrderEvent(List<String> cityList){
        this.cityList = cityList;
    }
    public List<String> getCityList() {
        return cityList;
    }

}
