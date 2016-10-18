package com.example.phelps.simpleforecast.Http;

import com.example.phelps.simpleforecast.Activity.MainActivity;
import com.example.phelps.simpleforecast.Data.WeatherData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Phelps on 2016/8/17.
 */
public class HttpMethods {
    private static final String BASE_URL = "https://api.heweather.com/x3/";
    private static String heFengKey = "00f40c947c99410b982f03996c7d1a7c";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private RetrofitService retrofitService;

    public HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public void getWeather(Subscriber<WeatherData> subscriber,String cityName){
        retrofitService.getWeather(cityName,heFengKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
