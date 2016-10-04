package com.example.phelps.simpleforecast.Base;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Phelps on 2016/10/4.
 */

public class SharedPreferencesHelper {

    private static final String PERF_NAME = "myCitys";
    private volatile static SharedPreferencesHelper instance;
    private final SharedPreferences preferences;

    private SharedPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PERF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesHelper.class) {
                if (instance == null)
                    instance = new SharedPreferencesHelper(context);
            }
        }
        return instance;
    }

    public final void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public final String getString(String key) {
        return preferences.getString(key, "");
    }

    public final void deleteString(String key) {
        preferences.edit().remove(key).apply();
    }

    public final void putInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public final int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public final void clear() {
        preferences.edit().clear().apply();
    }

}
