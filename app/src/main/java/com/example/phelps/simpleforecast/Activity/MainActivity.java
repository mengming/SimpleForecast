package com.example.phelps.simpleforecast.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.phelps.simpleforecast.Adapter.IndicatorAdapter;
import com.example.phelps.simpleforecast.Base.RxBus;
import com.example.phelps.simpleforecast.Data.AppVersionData;
import com.example.phelps.simpleforecast.Data.CityChangeOrderEvent;
import com.example.phelps.simpleforecast.Data.CityEvent;
import com.example.phelps.simpleforecast.Data.CityDeleteEvent;
import com.example.phelps.simpleforecast.Data.FabEvent;
import com.example.phelps.simpleforecast.Data.WeatherData;
import com.example.phelps.simpleforecast.Fragment.AppUpdateDialog;
import com.example.phelps.simpleforecast.Fragment.CityControlDialog;
import com.example.phelps.simpleforecast.Fragment.NewCityDialog;
import com.example.phelps.simpleforecast.Http.HttpUpdate;
import com.example.phelps.simpleforecast.R;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.viewpagerindicator.TabPageIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.indicator)
    TabPageIndicator indicator;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    private List<String> cityList = new ArrayList<>();
    private LocationClient locationClient;
    private CompositeSubscription mCompositeSubscription;
    private IndicatorAdapter adapter;
    private String mCity;
    private MyLocationListener locationListener = new MyLocationListener();
    private SharedPreferences cityPreferences;
    private Bundle cityObject;
    private Subscriber<AppVersionData> versionSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        cityObject = intent.getBundleExtra("cityObject");
        rxBusObservers();
        getMyCitys();
        initView();
        initLocation();
        if (cityList.size() == 0) {
            getPermission();
        }
        checkUpdate();
    }

    private void checkUpdate() {
        versionSubscriber = new Subscriber<AppVersionData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(AppVersionData appVersionData) {
                if (appVersionData.getVersion().equals(getAppVersionName(getApplicationContext()))) {
                    return;
                }
                else {
                    AppUpdateDialog appUpdateDialog = new AppUpdateDialog();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", appVersionData);
                    appUpdateDialog.setArguments(bundle);
                    appUpdateDialog.show(getSupportFragmentManager(), "appUpdateDialog");
                }
            }
        };
        HttpUpdate.getInstance().getUpdate(versionSubscriber);
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() == 0) return "";
        } catch (Exception e) {

        }
        return versionName;
    }

    private void initView() {
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCityDialog newCityDialog = new NewCityDialog();
                newCityDialog.setArguments(cityObject);
                newCityDialog.show(getSupportFragmentManager(), "cityDialog");
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        adapter = new IndicatorAdapter(getSupportFragmentManager(), cityList);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
    }

    @Override
    protected void onDestroy() {
        saveMyCity(mCity);
        super.onDestroy();
    }

    private boolean isCityCreated(String city) {
        for (int i = 0; i < cityList.size(); i++) {
            if (city.equals(cityList.get(i))) return true;
        }
        return false;
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE)
                    .subscribe(granted -> {
                        if (granted) {
                            locationClient.start();
                        } else {
                            Toast.makeText(this, "权限未配置", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            locationClient.start();
        }
    }

    private void initLocation() {
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(locationListener);
    }

    private void saveMyCity(String mCity) {
        SharedPreferences.Editor editor = cityPreferences.edit();
        editor.clear();
        for (int i = 0; i < cityList.size(); i++) {
            editor.putString("city" + i, cityList.get(i));
        }
        editor.remove("size");
        editor.putInt("size", cityList.size());
        editor.putString("mCity", mCity);
        editor.commit();
    }

    private void getMyCitys() {
        cityPreferences = getSharedPreferences("myCitys", MODE_PRIVATE);
        int size = cityPreferences.getInt("size", 0);
        mCity = cityPreferences.getString("mCity", "");
        for (int i = 0; i < size; i++)
            cityList.add(cityPreferences.getString("city" + i, ""));
    }

    private void rxBusObservers() {
        Subscription subscription = RxBus.getInstance()
                .toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event instanceof CityEvent) {
                            String strCity = ((CityEvent) event).getCityName();
                            if (isCityCreated(strCity)) {
                                int i = 0;
                                while (!cityList.get(i).equals(strCity)) i++;
                                pager.setCurrentItem(i);
                            } else {
                                cityList.add(strCity);
                                adapter.notifyDataSetChanged();
                                pager.setCurrentItem(cityList.size() - 1);
                                indicator.notifyDataSetChanged();
                            }
                        }
                        else if (event instanceof CityDeleteEvent) {
                            cityList.remove(((CityDeleteEvent) event).getIndex());
                            adapter.notifyDataSetChanged();
                            pager.setCurrentItem(0);
                            indicator.notifyDataSetChanged();
                        }
                        else if (event instanceof CityChangeOrderEvent) {
                            int start = ((CityChangeOrderEvent) event).getStart();
                            if (((CityChangeOrderEvent) event).getChangeOrder()) {
                                Collections.swap(cityList,start,start-1);
                            }
                            else Collections.swap(cityList,start,start+1);
                            adapter.notifyDataSetChanged();
                            pager.setCurrentItem(0);
                            indicator.notifyDataSetChanged();
                        }
                        else if (event instanceof FabEvent) {
                            if (((FabEvent) event).getIsShow() == 1) fab.hide();
                            else if (((FabEvent) event).getIsShow() == 0) fab.show();
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void addSubscription(Subscription subscription) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(subscription);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                Toast.makeText(getApplicationContext(),"无法定位到你的位置",Toast.LENGTH_SHORT).show();
                return;
            } else {
                mCity = bdLocation.getCity();
                int index = mCity.indexOf("市");
                mCity = mCity.substring(0, index);
                Toast.makeText(getApplicationContext(), mCity, Toast.LENGTH_SHORT).show();
                locationClient.stop();
                if (RxBus.getInstance().hasObservers()) {
                    RxBus.getInstance().post(new CityEvent(mCity));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_city_control) {
            showCityControl();
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void showCityControl() {
        CityControlDialog cityControlDialog = new CityControlDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cityList", (Serializable) cityList);
        cityControlDialog.setArguments(bundle);
        cityControlDialog.show(getFragmentManager(),"cityControl");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_location) {
            getPermission();
        } else if (id == R.id.nav_send) {
            Toast.makeText(getApplicationContext(), "反馈信息", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
