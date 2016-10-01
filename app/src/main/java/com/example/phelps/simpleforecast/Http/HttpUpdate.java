package com.example.phelps.simpleforecast.Http;

import com.example.phelps.simpleforecast.Data.AppVersionData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Phelps on 2016/10/1.
 */

public class HttpUpdate {

    private Retrofit retrofit;
    public static final String APK_URL = "http://119.29.186.49:8000/v1/";
    private RetrofitService retrofitService;
    private static final int DEFAULT_TIMEOUT = 5;

    private volatile static HttpUpdate INSTANCE;

    private HttpUpdate(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(APK_URL)
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    //获取单例
    public static HttpUpdate getInstance(){
        if (INSTANCE == null) {
            synchronized (HttpUpdate.class){
                INSTANCE = new HttpUpdate();
            }
        }
        return INSTANCE;
    }

    public void getUpdate(Subscriber<AppVersionData> subscriber){
        retrofitService.getUpdate()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
