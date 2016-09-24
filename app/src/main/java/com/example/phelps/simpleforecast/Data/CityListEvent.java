package com.example.phelps.simpleforecast.Data;

import java.util.ArrayList;

/**
 * Created by Phelps on 2016/9/25.
 */

public class CityListEvent {
//    public ArrayList<String> cityList;
//
//    public CityListEvent(ArrayList<String> cityList) {
//        this.cityList = cityList;
//    }
//
//    public ArrayList<String> getCityList(){
//        return cityList;
//    }
    public int index;
    public CityListEvent(int index) {
        this.index = index;
    }
    public int getIndex(){
        return index;
    }
}
