package com.example.phelps.simpleforecast.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phelps.simpleforecast.Adapter.RecyclerAdapter;
import com.example.phelps.simpleforecast.Data.WeatherData;
import com.example.phelps.simpleforecast.Http.HttpMethods;
import com.example.phelps.simpleforecast.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by Phelps on 2016/8/16.
 */
public class CityFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private Subscriber<WeatherData> subscriber;
    private RecyclerAdapter recyclerAdapter;
    private String cityName;
    private List<WeatherData.HeWeatherDataServiceBean> weatherDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        Bundle bundle = getArguments();
        cityName = bundle.getString("city");
        ButterKnife.bind(this, view);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                HttpMethods.getInstance().getWeather(subscriber,cityName);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapter = new RecyclerAdapter(getContext(),weatherDataList);
        recycler.setAdapter(recyclerAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscriber = new Subscriber<WeatherData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(WeatherData weatherData) {
                weatherDataList.clear();
                weatherDataList.addAll(weatherData.getHeWeatherDataService());
                recyclerAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        };
        HttpMethods.getInstance().getWeather(subscriber, cityName);
    }

}
