package com.kesari.trackingfresh.DeliveryAddress;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.List;
import java.util.Locale;

public class Add_DeliveryAddress extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt
{

    Button confirmAddress;
    EditText name,email,mobile,city,state,pincode,flat_no,building_name,landmark;
    private String TAG = this.getClass().getSimpleName();
    //private GPSTracker gpsTracker;
    private Location Current_Origin;
    private NetworkUtilsReceiver networkUtilsReceiver;
    String FullName = "";
    String city_geo = "";
    String state_geo = "";
    String country_geo = "";
    String addressZero = "";
    String addressOne = "";
    String addressTwo = "";
    String Country = "";
    String FeatureName = "";
    String AdminArea = "";
    String CountryCode = "";
    String Locality = "";
    String postalCode = "";
    String subAdminArea = "";
    String subLocality = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_address);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(Add_DeliveryAddress.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            confirmAddress = (Button) findViewById(R.id.confirmAddress);

            name = (EditText) findViewById(R.id.name);
            email = (EditText) findViewById(R.id.email);
            mobile = (EditText) findViewById(R.id.mobile);

            city = (EditText) findViewById(R.id.city);
            state = (EditText) findViewById(R.id.state);
            pincode = (EditText) findViewById(R.id.pincode);

            flat_no = (EditText) findViewById(R.id.flat_no);
            building_name = (EditText) findViewById(R.id.building_name);
            landmark = (EditText) findViewById(R.id.landmark);

            //gpsTracker = new GPSTracker(Add_DeliveryAddress.this);

            Current_Origin = SharedPrefUtil.getLocation(Add_DeliveryAddress.this);

            Double Lat = Current_Origin.getLatitude();
            Double Long = Current_Origin.getLongitude();

            FullName = SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getLastName();

            name.setText(FullName);
            email.setText(SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getEmailId());

            mobile.setText(SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getMobileNo());

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(Lat, Long, 1);

                addressZero = addresses.get(0).getAddressLine(0);
                addressOne = addresses.get(0).getAddressLine(1);
                addressTwo = addresses.get(0).getAddressLine(2);
                Country = addresses.get(0).getCountryName();
                FeatureName = addresses.get(0).getFeatureName();
                //String AddressLine = addresses.get(0).getAddressLine(0);
                AdminArea = addresses.get(0).getAdminArea();
                CountryCode = addresses.get(0).getCountryCode();
                Locality = addresses.get(0).getLocality();
                //String phone = addresses.get(0).getPhone();
                postalCode = addresses.get(0).getPostalCode();
                //String premises = addresses.get(0).getPremises();
                subAdminArea = addresses.get(0).getSubAdminArea();
                subLocality = addresses.get(0).getSubLocality();

                city_geo = Locality;
                state_geo = AdminArea;
                country_geo = Country;

                pincode.setText(postalCode);
                city.setText(subLocality + " , " + city_geo);
                state.setText(state_geo);

                Log.i("addressZero",addressZero);
                Log.i("addressOne",addressOne);
                Log.i("addressTwo",addressTwo);
                Log.i("Country",Country);
                Log.i("FeatureName",FeatureName);
                Log.i("AdminArea",AdminArea);
                Log.i("CountryCode",CountryCode);
                Log.i("Locality",Locality);
                Log.i("postalCode",postalCode);
                Log.i("subAdminArea",subAdminArea);
                Log.i("subLocality",subLocality);
                Log.i("city_geo",city_geo);
                Log.i("state_geo",state_geo);
                Log.i("country_geo",country_geo);

            } catch (Exception e) {
                e.printStackTrace();
            }

            confirmAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Add_DeliveryAddress.this,Default_DeliveryAddress.class);
                    intent.putExtra("FullName",FullName);
                    intent.putExtra("city",subLocality + " , " + city_geo);
                    intent.putExtra("postalCode",postalCode);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
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
                FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

}
