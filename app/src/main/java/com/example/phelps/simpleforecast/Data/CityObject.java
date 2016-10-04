package com.example.phelps.simpleforecast.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Phelps on 2016/8/22.
 */
public class CityObject {

    public HashMap<String, List<String>> cityMap;
    public List<String> provinceList;

    public CityObject(HashMap<String, List<String>> cityMap, List<String> provinceList){
        this.cityMap = cityMap;
        this.provinceList = provinceList;
    }
}
