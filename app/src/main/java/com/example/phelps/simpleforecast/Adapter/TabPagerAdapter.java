package com.example.phelps.simpleforecast.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.example.phelps.simpleforecast.Fragment.CityFragment;

import java.util.List;

/**
 * Created by Phelps on 2016/10/2.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private List<String> cityList;
    private List<Boolean> updateFlags;
    private FragmentManager fm;
    private Fragment newFragment;

    public void setNewFragment(Fragment newFragment) {
        this.newFragment = newFragment;
    }

    public void setUpdateFlags(List<Boolean> updateFlags) {
        this.updateFlags = updateFlags;
    }

    public TabPagerAdapter(FragmentManager fm, List<String> cityList, List<Boolean> updateFlags) {
        super(fm);
        this.fm = fm;
        this.cityList = cityList;
        this.updateFlags = updateFlags;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("city",cityList.get(position));
        CityFragment fragment = new CityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        if (updateFlags.size() != 0 && updateFlags.get(position)) {
            System.out.println(updateFlags.get(position)+""+position);
            String tag = fragment.getTag();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            fragment = newFragment;
            ft.add(container.getId(),fragment,tag);
            ft.attach(fragment);
            ft.commit();
            updateFlags.set(position,false);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return cityList.get(position);
    }
}
