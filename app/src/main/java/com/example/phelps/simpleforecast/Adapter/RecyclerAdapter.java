package com.example.phelps.simpleforecast.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phelps.simpleforecast.Data.WeatherData;
import com.example.phelps.simpleforecast.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phelps on 2016/8/17.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_SUGGESTION = 1;
    private static final int TYPE_DAILY = 2;
    private static final int TYPE_HOURLY = 3;

    private LayoutInflater inflater;
    private List<WeatherData.HeWeatherDataServiceBean> weatherDataList;
    private Context context;

    public RecyclerAdapter(Context context, List<WeatherData.HeWeatherDataServiceBean> list) {
        this.weatherDataList = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_TODAY) {
            View view = inflater.inflate(R.layout.card_today, parent, false);
            return new TodayHolder(view);
        } else if (viewType == TYPE_SUGGESTION) {
            View view = inflater.inflate(R.layout.card_suggestion, parent, false);
            return new SuggestionHolder(view);
        } else if (viewType == TYPE_DAILY) {
            View view = inflater.inflate(R.layout.card_daily, parent, false);
            return new DailyHolder(view);
        } else if (viewType == TYPE_HOURLY) {
            View view = inflater.inflate(R.layout.card_hourly, parent, false);
            return new HourlyHolder(view);
        } else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TODAY)
            ((TodayHolder) holder).bindData(weatherDataList);
        if (getItemViewType(position) == TYPE_SUGGESTION)
            ((SuggestionHolder) holder).bindData(weatherDataList);
        if (getItemViewType(position) == TYPE_DAILY)
            ((DailyHolder) holder).bindData(weatherDataList);
        if (getItemViewType(position) == TYPE_HOURLY)
            ((HourlyHolder) holder).bindData(weatherDataList);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_TODAY;
        else if (position ==1) return TYPE_SUGGESTION;
        else if (position == 2) return TYPE_DAILY;
        else if (position == 3) return TYPE_HOURLY;
        else return getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class TodayHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pic_weather)
        ImageView picWeather;
        @BindView(R.id.tv_weather_cond)
        TextView tvWeatherCond;
        @BindView(R.id.tv_temp_low)
        TextView tvTempLow;
        @BindView(R.id.tv_temp_now)
        TextView tvTempNow;
        @BindView(R.id.tv_temp_high)
        TextView tvTempHigh;
        @BindView(R.id.tv_feel_temp_data)
        TextView tvFeelTempData;
        @BindView(R.id.tv_air_aqi_data)
        TextView tvAirAqiData;
        @BindView(R.id.tv_air_qlty_data)
        TextView tvAirQltyData;

        public TodayHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(List<WeatherData.HeWeatherDataServiceBean> list) {
            if (list.size() != 0) {
                WeatherData.HeWeatherDataServiceBean.NowBean nowBean = list.get(0).getNow();
                WeatherData.HeWeatherDataServiceBean.AqiBean aqiBean = list.get(0).getAqi();
                WeatherData.HeWeatherDataServiceBean.DailyForecastBean dailyForecastBean = list.get(0).getDaily_forecast().get(0);
                tvWeatherCond.setText(nowBean.getCond().getTxt());
                if (aqiBean != null) {
                    tvAirAqiData.setText(aqiBean.getCity().getAqi());
                    tvAirQltyData.setText(aqiBean.getCity().getQlty());
                } else {
                    tvAirAqiData.setText("暂无数据");
                    tvAirQltyData.setText("暂无数据");
                }
                tvTempLow.setText(String.format("%s°C", dailyForecastBean.getTmp().getMin()));
                tvTempHigh.setText(String.format("%s°C", dailyForecastBean.getTmp().getMax()));
                tvTempNow.setText(String.format("%s°C", nowBean.getTmp()));
                Picasso.with(context).load(String.format("http://files.heweather.com/cond_icon/%s.png", nowBean.getCond().getCode())).into(picWeather);
                tvFeelTempData.setText(String.format("%s°C", nowBean.getFl()));
            }
        }
    }

    class SuggestionHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView[] picType = new AppCompatImageView[4];
        private TextView[] tvSuggestion = new TextView[4];
        private LinearLayout suggestionLinear;

        public SuggestionHolder(View itemView) {
            super(itemView);
            suggestionLinear = (LinearLayout) itemView.findViewById(R.id.suggestion_linear);
            for (int i=0; i<4; i++) {
                View view = View.inflate(context,R.layout.item_suggestion,null);
                picType[i] = (AppCompatImageView) view.findViewById(R.id.pic_type);
                tvSuggestion[i] = (TextView) view.findViewById(R.id.tv_suggestion);
                suggestionLinear.addView(view);
            }
        }

        public void bindData(List<WeatherData.HeWeatherDataServiceBean> weatherDataList) {
            if (weatherDataList.size() != 0) {
                WeatherData.HeWeatherDataServiceBean.SuggestionBean suggestionBean = weatherDataList.get(0).getSuggestion();
                if (suggestionBean != null) {
                    for (int i=0; i<4; i++) {
                        String string = null;
                        switch (i) {
                            case 0:
                                if (suggestionBean.getDrsg() != null) {
                                    string = suggestionBean.getDrsg().getTxt();
                                    picType[i].setImageResource(R.drawable.icon_cloth);
                                }
                                break;
                            case 1:
                                if (suggestionBean.getComf() != null) {
                                    string = suggestionBean.getComf().getTxt();
                                    picType[i].setImageResource(R.drawable.icon_humidity);
                                }
                                break;
                            case 2:
                                if (suggestionBean.getFlu() != null) {
                                    string = suggestionBean.getFlu().getTxt();
                                    picType[i].setImageResource(R.drawable.icon_flu);
                                }
                                break;
                            case 3:
                                if (suggestionBean.getSport() != null) {
                                    string = suggestionBean.getSport().getTxt();
                                    picType[i].setImageResource(R.drawable.icon_sport);
                                }
                                break;
                        }
                        tvSuggestion[i].setText(string);
                    }
                }
            }
            else {

            }
        }
    }

    class DailyHolder extends RecyclerView.ViewHolder {

        private LinearLayout dailyLinear;
        private ImageView[] picDailyCondDay = new ImageView[7];
        private ImageView[] picDailyCondNight = new ImageView[7];
        private TextView[] tvDailyCond = new TextView[7];
        private TextView[] tvDailyDate = new TextView[7];
        private TextView[] tvDailyTemp = new TextView[7];
        private TextView[] tvDailyPopData = new TextView[7];
        private TextView[] tvDailyHumData = new TextView[7];

        public DailyHolder(View itemView) {
            super(itemView);
            dailyLinear = (LinearLayout) itemView.findViewById(R.id.daily_linear);
            for (int i=0; i<7; i++) {
                View view = View.inflate(context,R.layout.item_daily,null);
                picDailyCondDay[i] = (ImageView) view.findViewById(R.id.pic_daily_cond_day);
                picDailyCondNight[i] = (ImageView) view.findViewById(R.id.pic_daily_cond_night);
                tvDailyCond[i] = (TextView) view.findViewById(R.id.tv_daily_cond);
                tvDailyDate[i] = (TextView) view.findViewById(R.id.tv_daily_date);
                tvDailyTemp[i] = (TextView) view.findViewById(R.id.tv_daily_temp);
                tvDailyPopData[i] = (TextView) view.findViewById(R.id.tv_daily_pop_data);
                tvDailyHumData[i] = (TextView) view.findViewById(R.id.tv_daily_hum_data);
                dailyLinear.addView(view);
            }
        }

        public void bindData(List<WeatherData.HeWeatherDataServiceBean> list) {
            if (list.size() != 0) {
                for (int i=0; i<7; i++) {
                    WeatherData.HeWeatherDataServiceBean.DailyForecastBean bean = list.get(0).getDaily_forecast().get(i);
                    Picasso.with(context).load(String.format("http://files.heweather.com/cond_icon/%s.png", bean.getCond().getCode_d())).into(picDailyCondDay[i]);
                    Picasso.with(context).load(String.format("http://files.heweather.com/cond_icon/%s.png", bean.getCond().getCode_n())).into(picDailyCondNight[i]);
                    String strCond = null;
                    if (bean.getCond().getCode_d().equals(bean.getCond().getCode_n()))
                        strCond = bean.getCond().getTxt_d();
                    else
                        strCond = String.format("%s转%s", bean.getCond().getTxt_d(), bean.getCond().getTxt_n());
                    tvDailyCond[i].setText(strCond);
                    String strDate = null;
                    if (i == 0) strDate = "今天";
                    else if (i == 1) strDate = "明天";
                    else strDate = bean.getDate().substring(5);
                    tvDailyDate[i].setText(strDate);
                    tvDailyTemp[i].setText(String.format("%s°C～%s°C", bean.getTmp().getMin(), bean.getTmp().getMax()));
                    tvDailyHumData[i].setText(String.format("%s%%", bean.getHum()));
                    tvDailyPopData[i].setText(String.format("%s%%", bean.getPop()));
                }
            }
        }
    }

    class HourlyHolder extends RecyclerView.ViewHolder {
        private LinearLayout hourlyLinear;
        int size = weatherDataList.size() == 0 ? 0 : weatherDataList.get(0).getHourly_forecast().size();
        private TextView[] hourlyTime = new TextView[size];
        private TextView[] hourlyTemp = new TextView[size];
        private TextView[] hourlyWet = new TextView[size];
        private TextView[] hourlyWind = new TextView[size];

        public HourlyHolder(View itemView) {
            super(itemView);
            hourlyLinear = (LinearLayout) itemView.findViewById(R.id.hourly_linear);
            for (int i = 0; i < size; i++) {
                View view = View.inflate(context, R.layout.item_hourly, null);
                hourlyTime[i] = (TextView) view.findViewById(R.id.tv_hourly_time);
                hourlyTemp[i] = (TextView) view.findViewById(R.id.tv_hourly_temp);
                hourlyWet[i] = (TextView) view.findViewById(R.id.tv_hourly_wet);
                hourlyWind[i] = (TextView) view.findViewById(R.id.tv_hourly_wind);
                hourlyLinear.addView(view);
            }
        }

        public void bindData(List<WeatherData.HeWeatherDataServiceBean> list) {
            if (list.size() != 0) {
                List<WeatherData.HeWeatherDataServiceBean.HourlyForecastBean> hourly = list.get(0).getHourly_forecast();
                for (int i = 0; i < size; i++) {
                    WeatherData.HeWeatherDataServiceBean.HourlyForecastBean bean = hourly.get(i);
                    hourlyTime[i].setText(bean.getDate().split(" ")[1]);
                    hourlyTemp[i].setText(String.format("%s°",bean.getTmp()));
                    hourlyWet[i].setText(String.format("%s%%",bean.getHum()));
                    String strWind;
                    if (bean.getWind().getSc().equals("微风")) strWind = bean.getWind().getDir()+bean.getWind().getSc();
                    else strWind = String.format("%s%s级",bean.getWind().getDir(),bean.getWind().getSc());
                    hourlyWind[i].setText(strWind);
                }
            }
        }
    }
}
