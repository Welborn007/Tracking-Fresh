package com.kesari.trackingfresh.DeliveryAddress;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kesari.trackingfresh.Map.GPSTracker;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

import java.util.List;
import java.util.Locale;

public class Add_DeliveryAddress extends AppCompatActivity {

    Button confirmAddress;
    EditText name,email,mobile,city,state,pincode,flat_no,building_name,landmark;

    private GPSTracker gpsTracker;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        gpsTracker = new GPSTracker(Add_DeliveryAddress.this);

        Double Lat = gpsTracker.getLatitude();
        Double Long = gpsTracker.getLongitude();

        FullName = SharedPrefUtil.getUser(Add_DeliveryAddress.this).getUser().getFirstName() + " " + SharedPrefUtil.getUser(Add_DeliveryAddress.this).getUser().getLastName();

        name.setText(FullName);
        email.setText(SharedPrefUtil.getUser(Add_DeliveryAddress.this).getUser().getEmailId());

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
}
