package com.example.phelps.simpleforecast.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.phelps.simpleforecast.Fragment.CityFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.List;

/**
 * Created by Phelps on 2016/8/16.
 */
public class IndicatorAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
    private List<String> cityList;
    private int mCount;

    public IndicatorAdapter(FragmentManager fm,List<String> cityList) {
        super(fm);
        this.cityList = cityList;
        mCount = cityList.size();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new CityFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city",cityList.get(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getIconResId(int index) {
        return 0;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return cityList.get(position);
    }

    public void setCount(int count) {

    }
}
