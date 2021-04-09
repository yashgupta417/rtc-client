package com.roomify_app.rtc_client.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    private static SharedPreferences getPreferences(Application application){
        SharedPreferences preferences=application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
        return preferences;
    }

    public static void saveString(String key, String value, Application application){
        getPreferences(application).edit().putString(key,value).apply();
    }

    public static String getString(String key,Application application){
        return getPreferences(application).getString(key,null);
    }

    public static void removeString(String key,Application application){
        getPreferences(application).edit().remove(key).apply();
    }
}
