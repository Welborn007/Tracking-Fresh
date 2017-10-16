package com.kesari.trackingfresh.TKCash;

import android.app.FragmentTransaction;
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
import android.view.MenuItem;
import android.widget.TextView;

import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.SlidingTabs.SlidingTabsBasicFragment;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TKWalletActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();

    private NetworkUtilsReceiver networkUtilsReceiver;
    TextView walletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tkwallet);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.porcelain));

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(TKWalletActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            walletAmount = (TextView) findViewById(R.id.walletAmount);

            getProfileData();

            if (savedInstanceState == null)
            {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
                transaction.replace(R.id.sample_content_fragment, fragment);
                transaction.commit();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(TKWalletActivity.this));

            ioUtils.getPOSTStringRequestHeader(TKWalletActivity.this, Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);
                    profileDataResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponse(String Response)
    {
        try
        {
            SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

            if(SharedPrefUtil.getUser(TKWalletActivity.this).getData().getWalletAmount() != null)
            {
                if(!SharedPrefUtil.getUser(TKWalletActivity.this).getData().getWalletAmount().isEmpty())
                {
                    walletAmount.setText(SharedPrefUtil.getUser(TKWalletActivity.this).getData().getWalletAmount());
                }
                else
                {
                    walletAmount.setText("0");
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
