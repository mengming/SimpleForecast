package com.example.phelps.simpleforecast.Base;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Phelps on 2016/10/4.
 */

public class SharedPreferencesHelper {

    private static final String PERF_NAME = "myCitys";
    private final SharedPreferences preferences;

    public SharedPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PERF_NAME, Context.MODE_PRIVATE);
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
