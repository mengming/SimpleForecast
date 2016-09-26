package com.example.phelps.simpleforecast.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phelps.simpleforecast.Base.RxBus;
import com.example.phelps.simpleforecast.Data.CityChangeOrderEvent;
import com.example.phelps.simpleforecast.Data.CityDeleteEvent;
import com.example.phelps.simpleforecast.R;

import java.util.ArrayList;

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

        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_city_control,null);
                viewHolder = new ViewHolder();
                viewHolder.cityText = (TextView) convertView.findViewById(R.id.tv_item_city_control);
                viewHolder.btnDelete = (ImageButton) convertView.findViewById(R.id.btn_city_delete);
                viewHolder.btnCityUp = (ImageButton) convertView.findViewById(R.id.btn_city_up);
                viewHolder.btnCityDown = (ImageButton) convertView.findViewById(R.id.btn_city_down);
                convertView.setTag(viewHolder);
            }
            else viewHolder = (ViewHolder) convertView.getTag();
            if (position == 0) viewHolder.btnCityUp.setEnabled(false);
            if (position == cityList.size()) viewHolder.btnCityDown.setEnabled(false);
            viewHolder.cityText.setText(cityList.get(position).toString());
            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RxBus.getInstance().hasObservers()) {
                        RxBus.getInstance().post(new CityDeleteEvent(position));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            viewHolder.btnCityUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RxBus.getInstance().hasObservers()) {
                        RxBus.getInstance().post(new CityChangeOrderEvent(position,position-1));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            viewHolder.btnCityDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RxBus.getInstance().hasObservers()) {
                        RxBus.getInstance().post(new CityChangeOrderEvent(position,position+1));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    public static class ViewHolder{
        public TextView cityText;
        public ImageButton btnDelete;
        public ImageButton btnCityUp;
        public ImageButton btnCityDown;
    }
}