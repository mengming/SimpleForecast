package com.example.phelps.simpleforecast.Http;

import android.os.Environment;

import com.example.phelps.simpleforecast.Data.AppVersionData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

    public void getApk(String url,Subscriber<File> subscriber){
        System.out.println("get");
        retrofitService.downloadApk(url)
                .map(new Func1<ResponseBody, File>() {
                    @Override
                    public File call(ResponseBody responseBody) {
                        System.out.println("call");
                        FileOutputStream outputStream = null;
                        try {
                            byte[] bytes = responseBody.bytes();
                            File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "app-release.apk");
                            outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            System.out.println("file");
                            return file;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("null");
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getUpdate(Subscriber<AppVersionData> subscriber){
        retrofitService.getUpdate()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
