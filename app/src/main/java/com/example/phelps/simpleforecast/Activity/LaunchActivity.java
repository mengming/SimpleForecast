package com.example.phelps.simpleforecast.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.example.phelps.simpleforecast.Base.JsonReader;
import com.example.phelps.simpleforecast.Base.SharedPreferencesHelper;
import com.example.phelps.simpleforecast.Data.CityData;
import com.example.phelps.simpleforecast.Data.CityObject;
import com.example.phelps.simpleforecast.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LaunchActivity extends Activity {


    private List<CityData> cityDataList = new ArrayList<>();
    private JSONObject cityObject;
    private JSONArray cityArray;
    private HashMap<String, List<String>> cityMap;
    private List<String> provinceList;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = LaunchActivity.this.getWindow();
        window.setFlags(flag,flag);
        setContentView(R.layout.activity_launch);

        cityObservable();

    }

    private void cityObservable() {
        Observable.create(new Observable.OnSubscribe<CityObject>() {
            @Override
            public void call(Subscriber<? super CityObject> subscriber) {
                JsonReader jsonReader = new JsonReader();
                cityObject = jsonReader.jsonObject();
                try {
                    cityArray = cityObject.getJSONArray("city_info");
                    for (int i = 0; i < cityArray.length(); i++) {
                        Gson gson = new Gson();
                        JSONObject city = (JSONObject) cityArray.get(i);
                        cityDataList.add(gson.fromJson(city.toString(), CityData.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                provinceList = getProvinceList();
                cityMap = getCityMap();
                CityObject cityObject = new CityObject(cityMap, provinceList);
                subscriber.onNext(cityObject);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CityObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CityObject cityObject) {
                        bundle = new Bundle();
                        bundle.putSerializable("province", (Serializable) cityObject.provinceList);
                        bundle.putSerializable("city", cityObject.cityMap);
                        Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                        intent.putExtra("cityObject",bundle);
                        startActivity(intent);
                        finish();
                    }
                });

    }

    private List<String> getProvinceList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < cityDataList.size(); i++) {
            CityData cityData = cityDataList.get(i);
            boolean isProvince = false;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).equals(cityData.getProv())) {
                    isProvince = true;
                    break;
                }
            }
            if (!isProvince) list.add(cityData.getProv());
        }
        return list;
    }


    private HashMap<String, List<String>> getCityMap() {
        HashMap<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < provinceList.size(); i++) {
            String prov = provinceList.get(i);
            List<String> list = new ArrayList<>();
            for (int j = 0; j < cityDataList.size(); j++) {
                if (cityDataList.get(j).getProv().equals(prov))
                    list.add(cityDataList.get(j).getCity());
            }
            map.put(prov, list);
        }
        return map;
    }

    private void getMyCitys() {
    }
}
