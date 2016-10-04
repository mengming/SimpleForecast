package com.example.phelps.simpleforecast.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.example.phelps.simpleforecast.Base.GetVersionName;
import com.example.phelps.simpleforecast.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_about_version)
    TextView tvAboutVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("关于SimpleForecast");
            bar.setDisplayHomeAsUpEnabled(true);
        }

        GetVersionName getVersionName = new GetVersionName(this);
        tvAboutVersion.append(getVersionName.versionName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //当点击不同的menu item 是执行不同的操作
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
