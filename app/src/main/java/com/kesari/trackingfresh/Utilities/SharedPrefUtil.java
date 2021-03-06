package com.kesari.trackingfresh.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.google.gson.Gson;
import com.kesari.trackingfresh.Login.LoginMain;

/**
 * Created by kesari on 26/04/17.
 */

public class SharedPrefUtil {
    public static String PREF_NAME = "Media";
    private static String KEY_USER = "user";
    private static String KEY_LAT = "latitude";
    private static String KEY_LONGI = "longitude";

    public static LoginMain getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_USER, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, LoginMain.class);
    }


    public static void setUser(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER, value).apply();

    }

    public static void setLocation(Context context, float lat, float lon) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putFloat(KEY_LAT, lat).putFloat(KEY_LONGI, lon).commit();
    }

    public static Location getLocation(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(preferences.getFloat(KEY_LAT, 0.0f));
        location.setLongitude(preferences.getFloat(KEY_LONGI, 0.0f));

        return location;
    }

    public static void setClear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_USER).commit();
    }

}
