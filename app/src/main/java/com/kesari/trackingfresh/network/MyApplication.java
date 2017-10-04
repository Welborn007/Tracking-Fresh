package com.kesari.trackingfresh.network;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.Default_DeliveryAddress;
import com.kesari.trackingfresh.DetailPage.DetailsActivity;
import com.kesari.trackingfresh.MyOffers.MyOffersActivity;
import com.kesari.trackingfresh.MyProfile.ProfileActivity;
import com.kesari.trackingfresh.Order.OrderReview;
import com.kesari.trackingfresh.ReferEarn.ReferralCodeActivity;
import com.kesari.trackingfresh.Settings.MyCards.CardPOJO;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.YourOrders.OrderListActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
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

    public ArrayList<CardPOJO> cardPOJOs = new ArrayList<CardPOJO>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        initFresco();
        Fabric.with(this, new Crashlytics());

        Gson gson = new Gson();
        Type type = new TypeToken<List<AddCart_model>>(){}.getType();
        ArrayList<AddCart_model> Products = gson.fromJson(SharedPrefUtil.getKeyUserCartItem(getApplicationContext()), type);

        if(Products != null)
        {
            if(!Products.isEmpty())
            {
                myProducts = Products;
            }
        }

        Type type1 = new TypeToken<List<CardPOJO>>(){}.getType();
        ArrayList<CardPOJO> cardPOJOs1 = gson.fromJson(SharedPrefUtil.getKeySavedCards(getApplicationContext()), type1);

        if(cardPOJOs1 != null)
        {
            if(!cardPOJOs1.isEmpty())
            {
                cardPOJOs = cardPOJOs1;
            }
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
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

    public void addCards(CardPOJO cardPOJO) {
        cardPOJOs.add(cardPOJO);
        saveCards();
    }

    public void removeCards(int Position) {
        cardPOJOs.remove(Position);
        saveCards();
    }

    public ArrayList<CardPOJO> getCardList() {

        Gson gson = new Gson();
        Type type = new TypeToken<List<CardPOJO>>(){}.getType();
        ArrayList<CardPOJO> cardPOJOs = gson.fromJson(SharedPrefUtil.getKeySavedCards(getApplicationContext()), type);

        return cardPOJOs;
    }

    public void saveCards()
    {
        //Set the values
        Gson gson = new Gson();
        String jsonText = gson.toJson(cardPOJOs);

        SharedPrefUtil.setKeySavedCards(getApplicationContext(),jsonText);
    }

    public void removeAllCards() {
        cardPOJOs.clear();
    }


    public AddCart_model getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(AddCart_model Products) {

        myProducts.add(Products);
        saveCart();
    }

    public String getProductQuantity(String product_id)
    {

        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        for (AddCart_model item : myProducts) {
            if (item.getProductId().equals(product_id)) {
                saveCart();
                return String.valueOf(item.getQuantity());
            }
        }
        return "0";
    }

    public boolean checkifproductexists(String product_id)
    {

        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        for (AddCart_model item : myProducts) {
            if (item.getProductId().equals(product_id)) {
                saveCart();
                //item.setQuantity(item.getQuantity() + 1);
                return true;
            }
        }
        return false;
    }

    public boolean IncrementProductQuantity(String product_id)
    {
        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        for (AddCart_model item : myProducts) {
            if (item.getProductId().equals(product_id)) {

                item.setQuantity(item.getQuantity() + 1);
                saveCart();
                return true;
            }
        }
        return false;
    }

    public boolean DecrementProductQuantity(String product_id)
    {
        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        for (AddCart_model item : myProducts) {
            if (item.getProductId().equals(product_id)) {

                item.setQuantity(item.getQuantity() - 1);
                saveCart();
                return true;
            }
        }
        return false;
    }

    public boolean RemoveProductonZeroQuantity(String product_id)
    {
        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        for (AddCart_model item : myProducts) {
            if (item.getProductId().equals(product_id)) {

                int position = myProducts.indexOf(item);

                myProducts.remove(position);
                saveCart();
                return true;
            }
        }
        return false;
    }

    public void removeProducts(int Position) {

        myProducts.remove(Position);
        saveCart();
    }

    public void removeProductsItems() {

        myProducts.clear();
        saveCart();
    }

    public ArrayList<AddCart_model> getProductsArraylist() {

        //DashboardActivity.updateNotificationsBadge(myProducts.size());

        Gson gson = new Gson();
        Type type = new TypeToken<List<AddCart_model>>(){}.getType();
        ArrayList<AddCart_model> Products = gson.fromJson(SharedPrefUtil.getKeyUserCartItem(getApplicationContext()), type);

        return Products;
    }

    public void saveCart()
    {
        //Set the values
        Gson gson = new Gson();
        String jsonText = gson.toJson(myProducts);

        DashboardActivity.updateNotificationsBadge(myProducts.size());
        AddToCart.updateNotificationsBadge(myProducts.size());
        Default_DeliveryAddress.updateNotificationsBadge(myProducts.size());
        DetailsActivity.updateNotificationsBadge(myProducts.size());
        ProfileActivity.updateNotificationsBadge(myProducts.size());
        OrderReview.updateNotificationsBadge(myProducts.size());
        OrderListActivity.updateNotificationsBadge(myProducts.size());
        ReferralCodeActivity.updateNotificationsBadge(myProducts.size());
        MyOffersActivity.updateNotificationsBadge(myProducts.size());

        SharedPrefUtil.setKeyUserCartItem(getApplicationContext(),jsonText);
    }
}
