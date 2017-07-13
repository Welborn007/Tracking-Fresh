package com.kesari.trackingfresh.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.google.gson.Gson;
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.NearestVehicleMainPOJO;
import com.kesari.trackingfresh.Login.ProfileMain;

/**
 * Created by kesari on 26/04/17.
 */

public class SharedPrefUtil {
    public static String PREF_NAME = "Media";
    private static String KEY_USER = "user";
    private static String KEY_LAT = "latitude";
    private static String KEY_LONGI = "longitude";

    public static String KEY_USER_TOKEN = "token";
    public static String KEY_USER_CART_ITEM = "cart";

    private static String KEY_VEHICLE = "vehicle";

    private static String KEY_LAT_DEFAULT = "latitude_def";
    private static String KEY_LONGI_DEFAULT = "longitude_def";

    public static ProfileMain getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_USER, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, ProfileMain.class);
    }


    public static void setUser(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER, value).apply();

    }

    public static void setToken(Context context, String Token) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER_TOKEN, Token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String Token = preferences.getString(KEY_USER_TOKEN, "");

        return Token;
    }

    public static void setKeyUserCartItem(Context context, String cartItems) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER_CART_ITEM, cartItems).apply();
    }

    public static String getKeyUserCartItem(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String Token = preferences.getString(KEY_USER_CART_ITEM, "");

        return Token;
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

    public static void setDefaultLocation(Context context, float lat, float lon) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putFloat(KEY_LAT_DEFAULT, lat).putFloat(KEY_LONGI_DEFAULT, lon).commit();
    }

    public static Location getDefaultLocation(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(preferences.getFloat(KEY_LAT_DEFAULT, 0.0f));
        location.setLongitude(preferences.getFloat(KEY_LONGI_DEFAULT, 0.0f));

        return location;
    }

    public static NearestVehicleMainPOJO getNearestVehicle(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_VEHICLE, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, NearestVehicleMainPOJO.class);
    }


    public static void setNearestVehicle(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_VEHICLE, value).apply();

    }

    public static void setClear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_USER).remove(KEY_USER_TOKEN).remove(KEY_USER_CART_ITEM).remove(KEY_VEHICLE).remove(KEY_LAT_DEFAULT).remove(KEY_LONGI_DEFAULT).commit();
    }


}
