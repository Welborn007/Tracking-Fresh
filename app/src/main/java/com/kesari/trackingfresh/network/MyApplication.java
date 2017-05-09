package com.kesari.trackingfresh.network;

import android.app.ActivityManager;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.kesari.trackingfresh.AddToCart.AddCart_model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kesari on 05/11/16.
 */
public class MyApplication extends Application
{

    private static MyApplication mInstance;
    private RequestQueue requestQueue;
    public final static String TAG = MyApplication.class.getSimpleName();
    public ArrayList<AddCart_model> myProducts = new ArrayList<AddCart_model>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        initFresco();
    }

    private void initFresco() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig
                .newBuilder(getApplicationContext())
                .setBitmapMemoryCacheParamsSupplier(new LollipopBitmapMemoryCacheParamsSupplier(activityManager))
                .build();

        Fresco.initialize(getApplicationContext(), imagePipelineConfig);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public <T> void addRequestToQueue(Request<T> requestQueue, String tag) {
        requestQueue.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(requestQueue);


    }

    public <T> void addRequestToQueue(Request<T> requestQueue) {
        requestQueue.setTag(TAG);
        getRequestQueue().add(requestQueue);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }


    public AddCart_model getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(AddCart_model Products) {

        myProducts.add(Products);

    }

    public void removeProducts(int Position) {

        myProducts.remove(Position);

    }

    public void removeProductsItems() {

        myProducts.clear();

    }

    public ArrayList<AddCart_model> getProductsArraylist() {

        return myProducts;
    }
}
