package com.kesari.trackingfresh.DeliveryAddress.UpdateDeleteDeliveryAddress;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.google.gson.Gson;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.FetchAddressPOJO;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FetchedDeliveryAddressActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();

    public static List<AddressPOJO> addressArrayList = new ArrayList<>();

    public static Gson gson;
    public static FetchAddressPOJO fetchAddressPOJO;
    private NetworkUtilsReceiver networkUtilsReceiver;
    public static RecyclerView recListFecthedDeliveryAddress;
    private LinearLayoutManager AddressLayoutManager;
    public static RecyclerView.Adapter adapterAddress;
    private Button btnSubmit;
    public static  boolean default_address = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetched_delivery_address);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getBackground().setAlpha(0);

            gson = new Gson();

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            try {
                if(getIntent().getStringExtra("default_address").equalsIgnoreCase("false"))
                {
                    //FireToast.customSnackbar(FetchedDeliveryAddressActivity.this, "Default address not set!", "");

                    new SweetAlertDialog(FetchedDeliveryAddressActivity.this)
                            .setTitleText("Default address not set!")
                            .show();
                }
            }catch (NullPointerException npe)
            {

            }

            recListFecthedDeliveryAddress = (RecyclerView) findViewById(R.id.recyclerView);

            recListFecthedDeliveryAddress.setHasFixedSize(true);
            AddressLayoutManager = new LinearLayoutManager(FetchedDeliveryAddressActivity.this);
            AddressLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recListFecthedDeliveryAddress.setLayoutManager(AddressLayoutManager);

            /*recListFecthedDeliveryAddress.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {

                           *//*AddressPOJO addressPOJO = addressArrayList.get(position);

                            if(!addressPOJO.isDefault())
                            {
                                updateDeliveryAddress(addressPOJO.get_id(),position);
                            }
                            else
                            {
                                Toast.makeText(FetchedDeliveryAddressActivity.this, "Address already set default", Toast.LENGTH_SHORT).show();
                            }*//*

                            //Toast.makeText(FetchedDeliveryAddressActivity.this, "Clicked Whole", Toast.LENGTH_SHORT).show();
                        }
                    })
            );*/


            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(FetchedDeliveryAddressActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchUserAddress(FetchedDeliveryAddressActivity.this,TAG);
    }

    public static void fetchUserAddress(final Context context, final String TAG) {
        try {

            String url = Constants.FetchAddress;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    fetchUserAddressResponse(result,context,TAG);
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

    public static void fetchUserAddressResponse(String Response,Context context, final String TAG) {
        try {

            default_address = false;
            fetchAddressPOJO = gson.fromJson(Response, FetchAddressPOJO.class);

            if (fetchAddressPOJO.getData().isEmpty()) {
                Intent intent = new Intent(context, Add_DeliveryAddress.class);
                context.startActivity(intent);

                adapterAddress = new UpdateDeleteDeliveryAddress_RecyclerAdpater(fetchAddressPOJO.getData(),context);
                recListFecthedDeliveryAddress.setAdapter(adapterAddress);
            } else {

                addressArrayList = fetchAddressPOJO.getData();

                adapterAddress = new UpdateDeleteDeliveryAddress_RecyclerAdpater(fetchAddressPOJO.getData(),context);
                recListFecthedDeliveryAddress.setAdapter(adapterAddress);

                for (Iterator<AddressPOJO> it = addressArrayList.iterator(); it.hasNext(); ) {
                    AddressPOJO addressPOJO = it.next();

                    if (addressPOJO.isDefault())
                    {
                        default_address = true;
                    }
                    else
                    {

                    }

                }

                if(!default_address)
                {
                    //FireToast.customSnackbar(context, "Default address not set!", "");
                    //Toast.makeText(context,"Default address not set!", Toast.LENGTH_SHORT).show();

                    new SweetAlertDialog(context)
                            .setTitleText("Default address not set!")
                            .show();
                }

            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void updateDeliveryAddress(String addressID, final int position, final Context context) {
        try {

            String url = Constants.UpdateAddress;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", addressID);
                postObject.put("isDefault", "true");

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.sendJSONObjectPutRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Address Update", result.toString());
                    updateDeliveryAddressResponse(result,position,context);
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

    public static void updateDeliveryAddressResponse(String Response,int pos,Context context)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(Message.equalsIgnoreCase("Updated Successfully"))
            {
                adapterAddress.notifyDataSetChanged();
                fetchUserAddress(context,"Address Update");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if(default_address)
                {
                    finish();
                }
                else
                {
                    //FireToast.customSnackbar(FetchedDeliveryAddressActivity.this, "Default address not set!", "");

                    new SweetAlertDialog(FetchedDeliveryAddressActivity.this)
                            .setTitleText("Default address not set!")
                            .show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(default_address)
        {
            finish();
        }
        else
        {
            //FireToast.customSnackbar(FetchedDeliveryAddressActivity.this, "Default address not set!", "");

            new SweetAlertDialog(FetchedDeliveryAddressActivity.this)
                    .setTitleText("Default address not set!")
                    .show();
        }
    }

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
