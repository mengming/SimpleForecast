package com.example.phelps.simpleforecast.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.phelps.simpleforecast.Base.RxBus;
import com.example.phelps.simpleforecast.Data.CityData;
import com.example.phelps.simpleforecast.Data.CityEvent;
import com.example.phelps.simpleforecast.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Phelps on 2016/8/20.
 */
public class NewCityDialog extends DialogFragment {
    @BindView(R.id.province_wheel)
    WheelView provinceWheel;
    @BindView(R.id.city_wheel)
    WheelView cityWheel;
    @BindView(R.id.btn_city)
    Button btnCity;

    private HashMap<String, List<String>> cityMap;
    private List<String> provinceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_dialog, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        provinceList = (List<String>) bundle.getSerializable("province");
        cityMap = (HashMap<String, List<String>>) bundle.getSerializable("city");
        initWheelView();
        return view;
    }

    @OnClick(R.id.btn_city)
    public void onClick() {
        if (RxBus.getInstance().hasObservers()) {
            RxBus.getInstance().post(new CityEvent(cityWheel.getSelectionItem().toString()));
            dismiss();
        }
    }

    private void initWheelView() {
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextSize = 20;
        style.textSize = 16;
        style.textColor = Color.BLACK;
        provinceWheel.setWheelAdapter(new ArrayWheelAdapter(getContext()));
        provinceWheel.setSkin(WheelView.Skin.Holo);
        provinceWheel.setWheelSize(5);
        provinceWheel.setWheelData(provinceList);
        provinceWheel.setStyle(style);

        cityWheel.setWheelAdapter(new ArrayWheelAdapter(getContext()));
        cityWheel.setSkin(WheelView.Skin.Holo);
        cityWheel.setWheelData(cityMap.get(provinceList.get(provinceWheel.getSelection())));
        cityWheel.setStyle(style);
        cityWheel.setWheelSize(5);
        provinceWheel.join(cityWheel);
        provinceWheel.joinDatas(cityMap);
    }


}
