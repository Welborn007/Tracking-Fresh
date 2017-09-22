package com.kesari.trackingfresh.CheckNearestVehicleAvailability;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.VehicleNearestRoute.NearestRouteMainPOJO;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckVehicleActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    TextView search_text;
    AVLoadingIndicatorView avi,aviFailed;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    private Gson gson;
    //NearestVehicleMainPOJO nearestVehicleMainPOJO;
    NearestRouteMainPOJO nearestRouteMainPOJO;

    private Location Current_Location;
    private LatLng Current_Origin;
    ScheduledExecutorService scheduleTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_vehicle);

        try
        {

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            search_text = (TextView) findViewById(R.id.search_text);
            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            aviFailed = (AVLoadingIndicatorView) findViewById(R.id.aviFailed);

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(CheckVehicleActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }

                /*scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

                // This schedule a task to run every 10 minutes:
                scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {

                        Current_Location = SharedPrefUtil.getLocation(CheckVehicleActivity.this);
                        Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

                        Log.i("laittudeText",String.valueOf(Current_Origin.latitude));

                        if(!String.valueOf(Current_Origin.latitude).equalsIgnoreCase("0.0")  && !String.valueOf(Current_Origin.longitude).equalsIgnoreCase("0.0"))
                        {
                            sendLATLONVehicle();
                        }

                    }
                }, 0, 10, TimeUnit.SECONDS);*/

                sendLATLONVehicle();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
            sendLATLONVehicle();
        }
    }

    private void sendLATLONVehicle()
    {
        try
        {

            String url = Constants.VehicleNearestRoute ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("longitude", SharedPrefUtil.getLocation(CheckVehicleActivity.this).getLongitude());
                postObject.put("latitude", SharedPrefUtil.getLocation(CheckVehicleActivity.this).getLatitude());

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(CheckVehicleActivity.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(CheckVehicleActivity.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    //scheduleTaskExecutor.shutdown();
                    //NearestVehicleResponse(result);

                    NearestVehicleRouteResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void NearestVehicleRouteResponse(String Response)
    {
        try
        {
            nearestRouteMainPOJO = gson.fromJson(Response, NearestRouteMainPOJO.class);

            if(nearestRouteMainPOJO.getData().isEmpty())
            {
                search_text.setText("Sorry! We don't serve on this route currently");
                aviFailed.setVisibility(View.VISIBLE);
                avi.setVisibility(View.GONE);
                SharedPrefUtil.setNearestRouteMainPOJO(CheckVehicleActivity.this,"");

                Intent intent = new Intent(CheckVehicleActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                SharedPrefUtil.setNearestRouteMainPOJO(CheckVehicleActivity.this,Response);
                aviFailed.setVisibility(View.GONE);
                avi.setVisibility(View.VISIBLE);

                Intent intent = new Intent(CheckVehicleActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

   /* private void NearestVehicleResponse(String Response)
    {
        try
        {
            nearestVehicleMainPOJO = gson.fromJson(Response, NearestVehicleMainPOJO.class);

            if(nearestVehicleMainPOJO.getData().isEmpty())
            {
                search_text.setText("Oops! No Vehicle found Nearby!");
                aviFailed.setVisibility(View.VISIBLE);
                avi.setVisibility(View.GONE);
                SharedPrefUtil.setNearestVehicle(CheckVehicleActivity.this,"");

                Intent intent = new Intent(CheckVehicleActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();

                *//*final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 5000);*//*
            }
            else
            {
                SharedPrefUtil.setNearestVehicle(CheckVehicleActivity.this,Response);
                aviFailed.setVisibility(View.GONE);
                avi.setVisibility(View.VISIBLE);

                Intent intent = new Intent(CheckVehicleActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }

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
                /*FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
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
