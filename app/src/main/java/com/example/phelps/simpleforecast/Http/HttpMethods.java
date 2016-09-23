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
    public static final String BASE_URL = "https://api.heweather.com/x3/";
    private static String heFengKey = "00f40c947c99410b982f03996c7d1a7c";

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private MainActivity.WeatherService weatherService;

    private HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        weatherService = retrofit.create(MainActivity.WeatherService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void getWeather(Subscriber<WeatherData> subscriber,String cityName){
        weatherService.getWeather(cityName,heFengKey)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
