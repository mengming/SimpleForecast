package com.example.phelps.simpleforecast.Fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phelps.simpleforecast.Base.MyInterface;
import com.example.phelps.simpleforecast.Base.RxBus;
import com.example.phelps.simpleforecast.Data.CityListEvent;
import com.example.phelps.simpleforecast.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phelps on 2016/9/25.
 */

public class CityControlDialog extends DialogFragment {
    @BindView(R.id.city_control_list)
    ListView cityControlList;

    private ArrayList cityList;
    private CityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_control, container, false);
        Bundle bundle = getArguments();
        cityList = (ArrayList) bundle.getSerializable("cityList");
        ButterKnife.bind(this, view);
        adapter = new CityAdapter();
        cityControlList.setAdapter(adapter);
        return view;
    }

    private class CityAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_city_control,parent,false);
            TextView cityText = (TextView) view.findViewById(R.id.tv_item_city_control);
            cityText.setText(cityList.get(position).toString());
            Button button = (Button) view.findViewById(R.id.btn_city_delete);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RxBus.getInstance().hasObservers()) {
                        RxBus.getInstance().post(new CityListEvent(position));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return view;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
