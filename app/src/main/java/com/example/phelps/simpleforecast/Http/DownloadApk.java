package com.example.phelps.simpleforecast.Http;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;

/**
 * Created by Phelps on 2016/10/1.
 */

public class DownloadApk {
    private Retrofit retrofit;
    private RetrofitService retrofitService;
    private static final int DEFAULT_TIMEOUT = 5;

    private volatile static DownloadApk INSTANCE;

    public DownloadApk(String url){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    //获取单例
    public static DownloadApk getInstance(String url){
        if (INSTANCE == null) {
            synchronized (HttpUpdate.class){
                INSTANCE = new DownloadApk(url);
            }
        }
        return INSTANCE;
    }

    public void getApk(String url, Subscriber subscriber){
        try {
            Response<ResponseBody> response = retrofitService.downloadApk(url).execute();
            if (response != null && response.isSuccessful()) {
                //文件总长度
                long fileSize = response.body().contentLength();
                long fileSizeDownloaded = 0;
                InputStream is = response.body().byteStream();
                File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "app-release.apk");
                if (file.exists()) {
                    file.delete();
                }
                else {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                int count = 0;
                byte[] buffer = new byte[1024];
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                    fileSizeDownloaded += count;
                    subscriber.onNext("file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                fos.flush();
                subscriber.onCompleted();
            }
            else {
                subscriber.onError(new Exception("接口请求异常"));
            }
        } catch (IOException e) {
            subscriber.onError(e);
        }
    }
}
