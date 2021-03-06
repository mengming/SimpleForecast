package com.example.phelps.simpleforecast.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.example.phelps.simpleforecast.Adapter.TabPagerAdapter;
import com.example.phelps.simpleforecast.Base.GetVersionName;
import com.example.phelps.simpleforecast.Base.RxBus;
import com.example.phelps.simpleforecast.Base.SharedPreferencesHelper;
import com.example.phelps.simpleforecast.Data.AppVersionData;
import com.example.phelps.simpleforecast.Data.CityChangeOrderEvent;
import com.example.phelps.simpleforecast.Data.CityDeleteEvent;
import com.example.phelps.simpleforecast.Data.CityEvent;
import com.example.phelps.simpleforecast.Data.FabEvent;
import com.example.phelps.simpleforecast.Fragment.AppUpdateDialog;
import com.example.phelps.simpleforecast.Fragment.CityControlDialog;
import com.example.phelps.simpleforecast.Fragment.CityFragment;
import com.example.phelps.simpleforecast.Fragment.NewCityDialog;
import com.example.phelps.simpleforecast.Http.HttpUpdate;
import com.example.phelps.simpleforecast.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private List<String> cityList = new ArrayList<>();
    private List<Boolean> updateList = new ArrayList<>();
    private LocationClient locationClient;
    private CompositeSubscription mCompositeSubscription;
    private String mCity;
    private MyLocationListener locationListener = new MyLocationListener();
    private Bundle cityObject;
    private Subscriber<AppVersionData> versionSubscriber;
    private TabPagerAdapter tabPagerAdapter;
    private HttpUpdate httpUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        cityObject = intent.getBundleExtra("cityObject");
        rxBusObservers();
        SharedPreferencesHelper instance = new SharedPreferencesHelper(getApplicationContext());
        int size = instance.getInt("size");
        for (int i=0; i<size; i++) {
            String city = instance.getString("city"+i);
            cityList.add(city);
        }
        instance = null;
        initLocation();
        if (cityList.size() == 0) {
            getPermission();
        }
        initView();
        checkUpdate();
    }

    private Fragment newFragment(String cityName){
        Bundle bundle = new Bundle();
        bundle.putString("city",cityName);
        CityFragment fragment = new CityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void checkUpdate() {
        versionSubscriber = new Subscriber<AppVersionData>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(AppVersionData appVersionData) {
                System.out.println("onNext");
                GetVersionName getVersionName = new GetVersionName(getApplicationContext());
                String version = getVersionName.versionName;
                getVersionName = null;
                if (appVersionData.getVersion().equals(version)) {
                    Toast.makeText(getApplicationContext(), "已是最新版本了", Toast.LENGTH_SHORT).show();
                } else {
                    AppUpdateDialog appUpdateDialog = new AppUpdateDialog();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", appVersionData);
                    appUpdateDialog.setArguments(bundle);
                    appUpdateDialog.show(getSupportFragmentManager(), "appUpdateDialog");
                }
            }
        };
        httpUpdate = new HttpUpdate();
        httpUpdate.getUpdate(versionSubscriber);
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

        for (int i=0;i<cityList.size();i++) updateList.add(false);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(),cityList,updateList);
        pager.setAdapter(tabPagerAdapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStop() {
        saveMyCity();
        super.onStop();
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

    private void saveMyCity() {
        SharedPreferencesHelper instance = new SharedPreferencesHelper(getApplicationContext());
        instance.clear();
        for (int i=0; i<cityList.size(); i++) instance.putString("city"+i,cityList.get(i));
        instance.putInt("size", cityList.size());
        instance = null;
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
                                updateList.add(false);
                                tabPagerAdapter.notifyDataSetChanged();
                                pager.setCurrentItem(cityList.size() - 1);
                            }
                        } else if (event instanceof CityDeleteEvent) {
                            int index = ((CityDeleteEvent) event).getIndex();
                            cityList.remove(index);
                            tabPagerAdapter.notifyDataSetChanged();
                            pager.setCurrentItem(0);
                        } else if (event instanceof CityChangeOrderEvent) {
                            ArrayList<String> list = ((CityChangeOrderEvent) event).getCityList();
                            for (int i=0;i<list.size();i++) {
                                System.out.println(list.get(i)+"   "+cityList.get(i));
                                if (!list.get(i).equals(cityList.get(i))) {
                                    updateList.set(i, true);
                                    System.out.println("different");
                                }
                            }
                            cityList.clear();
                            cityList.addAll(list);
                            tabPagerAdapter.setUpdateFlags(updateList);
                            for (int i=0;i<list.size();i++)
                                if (updateList.get(i)) {
                                    System.out.println(i+"instantiate");
                                    System.out.println(list.get(i));
                                    tabPagerAdapter.setNewFragment(newFragment(list.get(i)));
                                    tabPagerAdapter.instantiateItem(pager,i);
                                }
                            tabPagerAdapter.notifyDataSetChanged();
                            pager.setCurrentItem(0);
                        } else if (event instanceof FabEvent) {
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
                Toast.makeText(getApplicationContext(), "无法定位到你的位置", Toast.LENGTH_SHORT).show();
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
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
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
        cityControlDialog.show(getFragmentManager(), "cityControl");
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
        } else if (id == R.id.nav_update) {
            httpUpdate.getUpdate(versionSubscriber);
            System.out.println("update");
        } else if (id == R.id.nav_about) {
            showAbout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAbout() {
        Intent intent = new Intent(MainActivity.this,AboutActivity.class);
        startActivity(intent);
    }

}
