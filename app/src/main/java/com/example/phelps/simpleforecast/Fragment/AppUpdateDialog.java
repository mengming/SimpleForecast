package com.example.phelps.simpleforecast.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phelps.simpleforecast.Base.GetVersionName;
import com.example.phelps.simpleforecast.Data.AppVersionData;
import com.example.phelps.simpleforecast.Http.HttpUpdate;
import com.example.phelps.simpleforecast.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Phelps on 2016/9/30.
 */

public class AppUpdateDialog extends DialogFragment {
    @BindView(R.id.tv_update_app_name)
    TextView tvUpdateAppName;
    @BindView(R.id.tv_update_change_log)
    TextView tvUpdateChangeLog;
    @BindView(R.id.btn_update_sure)
    AppCompatButton btnUpdateSure;
    @BindView(R.id.btn_update_cancel)
    AppCompatButton btnUpdateCancel;

    private AppVersionData appVersionData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_update, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        appVersionData = (AppVersionData) bundle.getSerializable("data");
        if (appVersionData != null) {
            tvUpdateAppName.setText(appVersionData.getName());
            tvUpdateChangeLog.setText(appVersionData.getChange_log());
        }
        this.setCancelable(false);
        return view;
    }

    @OnClick({R.id.btn_update_sure, R.id.btn_update_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_sure:
                download(appVersionData.getInstall_url());
                break;
            case R.id.btn_update_cancel: getDialog().dismiss();
                break;
        }
    }

    private void download(String url) {
        Subscriber<File> subscriber = new Subscriber<File>() {
            @Override
            public void onCompleted() {
                System.out.println("finish");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(File file) {
                if (file == null) System.out.println("null");
                else {
                    System.out.println("onNext");
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                }
            }
        };
        HttpUpdate httpUpdate = new HttpUpdate();
        httpUpdate.getApk(url,subscriber,appVersionData.getName(), new GetVersionName(getContext()).versionName);
    }

}
