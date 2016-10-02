package com.example.phelps.simpleforecast.Http;

import com.example.phelps.simpleforecast.Data.AppVersionData;
import com.example.phelps.simpleforecast.Data.WeatherData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Phelps on 2016/9/30.
 */

public interface RetrofitService {
    @GET("weather")
    Observable<WeatherData> getWeather(@Query("city") String city, @Query("key") String key);
    @GET("simpleForecast")
    Observable<AppVersionData> getUpdate();
    @Streaming
    @GET
    Observable<ResponseBody> downloadApk(@Url String url);
}
