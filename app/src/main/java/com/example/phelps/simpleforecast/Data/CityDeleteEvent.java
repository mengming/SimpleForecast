package com.example.phelps.simpleforecast.Data;

import java.util.ArrayList;

/**
 * Created by Phelps on 2016/9/25.
 */

public class CityDeleteEvent {
    public int index;
    public CityDeleteEvent(int index) {
        this.index = index;
    }
    public int getIndex(){
        return index;
    }
}
