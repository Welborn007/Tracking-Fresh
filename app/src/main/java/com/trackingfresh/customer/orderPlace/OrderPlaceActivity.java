package com.trackingfresh.customer.orderPlace;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.trackingfresh.customer.Map.LocationServiceNew;
import com.trackingfresh.customer.OrderTracking.OrderBikerTrackingActivity;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.network.NetworkUtils;
import com.trackingfresh.customer.network.NetworkUtilsReceiver;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by SNK Consulting on 15-10-2017.
 */

public class OrderPlaceActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    TextView orderNumberTextView;
    FancyButton trackOrderTextView;
    private String orderId,orderNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_place);


        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.porcelain));


            orderNumberTextView = (TextView) findViewById(R.id.orderNumberTextView);
            trackOrderTextView = (FancyButton) findViewById(R.id.trackOrderTextView);

            orderId = getIntent().getStringExtra("orderID");
            orderNO = getIntent().getStringExtra("orderNO");

            orderNumberTextView.setText("Order Number: "+orderNO);//
         /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                IOUtils.buildAlertMessageNoGps(OrderPlaceActivity.this);
            } else {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            trackOrderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderPlaceActivity.this, OrderBikerTrackingActivity.class);
                    intent.putExtra("orderID", orderId);
                    startActivity(intent);
                    finish();
                }
            });
        }catch (Exception e){

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);
            //scheduleTaskExecutor.shutdown();

            /*if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }*/

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }


    @Override
    public void NetworkOpen() {

    }

    @Override
    public void NetworkClose() {

        try {

            if (!NetworkUtils.isNetworkConnectionOn(this)) {
               /* FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;*/

                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Oops! No internet access")
                        .setContentText("Please Check Settings")
                        .setConfirmText("Enable the Internet?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

}
