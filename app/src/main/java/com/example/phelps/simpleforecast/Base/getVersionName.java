package com.example.phelps.simpleforecast.Base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Phelps on 2016/10/3.
 */

public class GetVersionName {
    public String versionName = "";

    public GetVersionName(Context context) {
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() == 0) versionName = "";
        } catch (Exception e) {

        }
    }
}
