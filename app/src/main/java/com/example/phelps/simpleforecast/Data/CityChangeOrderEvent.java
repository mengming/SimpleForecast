package com.example.phelps.simpleforecast.Data;

/**
 * Created by Phelps on 2016/9/26.
 */

public class CityChangeOrderEvent {
    int a,b,start;
    boolean changeOrder;
    public CityChangeOrderEvent(int a,int b){
        this.a = a;
        this.b = b;
        start = a;
        //向上为true，向下为false
        if (a>b) changeOrder = true;
        else changeOrder = false;
    }

    public boolean getChangeOrder() {
        return changeOrder;
    }

    public int getStart() {
        return start;
    }
}
