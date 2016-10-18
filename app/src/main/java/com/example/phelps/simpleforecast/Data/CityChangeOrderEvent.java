package com.example.phelps.simpleforecast.Data;

import com.example.phelps.simpleforecast.Fragment.CityFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phelps on 2016/9/26.
 */

public class CityChangeOrderEvent {

    private ArrayList<String> cityList;

    public CityChangeOrderEvent(ArrayList<String> cityList){
        this.cityList = cityList;
    }
    public ArrayList<String> getCityList() {
        return cityList;
    }

}
