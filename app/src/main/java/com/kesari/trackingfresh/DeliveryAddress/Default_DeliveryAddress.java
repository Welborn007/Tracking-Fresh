package com.kesari.trackingfresh.DeliveryAddress;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesari.trackingfresh.Map.GPSTracker;
import com.kesari.trackingfresh.Payment.PaymentDetails;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

import java.util.List;
import java.util.Locale;

public class Default_DeliveryAddress extends AppCompatActivity {

    Button btnSubmit,btnChange;
    private GPSTracker gpsTracker;

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

    TextView name,address,city,pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default__delivery_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnChange = (Button) findViewById(R.id.btnChange);

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        city = (TextView) findViewById(R.id.city);
        pincode = (TextView) findViewById(R.id.pincode);

        name.setText(getIntent().getStringExtra("FullName"));
        address.setText("B/201, Lourdes Park, Bolinj-Sopara Rd.");
        city.setText(getIntent().getStringExtra("city"));
        pincode.setText(getIntent().getStringExtra("postalCode"));

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDefaultAddress();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Default_DeliveryAddress.this, PaymentDetails.class);
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

    private void SetDefaultAddress()
    {
        // Create custom dialog object
        final Dialog dialog = new Dialog(Default_DeliveryAddress.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_default_address);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        TextView delivery_text;
        EditText name,email,mobile,flat_no,building_name,landmark,city,state,pincode;
        Button confirmAddress;

        delivery_text = (TextView) dialog.findViewById(R.id.delivery_text);
        name = (EditText) dialog.findViewById(R.id.name);
        email = (EditText) dialog.findViewById(R.id.email);
        mobile = (EditText) dialog.findViewById(R.id.mobile);
        flat_no = (EditText) dialog.findViewById(R.id.flat_no);
        building_name = (EditText) dialog.findViewById(R.id.building_name);
        landmark = (EditText) dialog.findViewById(R.id.landmark);
        city = (EditText) dialog.findViewById(R.id.city);
        state = (EditText) dialog.findViewById(R.id.state);
        pincode = (EditText) dialog.findViewById(R.id.pincode);

        confirmAddress = (Button) dialog.findViewById(R.id.confirmAddress);

        delivery_text.setText("Set new Default Delivery Address");
        name.setText(SharedPrefUtil.getUser(Default_DeliveryAddress.this).getUser().getFirstName() + " " + SharedPrefUtil.getUser(Default_DeliveryAddress.this).getUser().getLastName());
        email.setText(SharedPrefUtil.getUser(Default_DeliveryAddress.this).getUser().getEmailId());

        gpsTracker = new GPSTracker(Default_DeliveryAddress.this);

        Double Lat = gpsTracker.getLatitude();
        Double Long = gpsTracker.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Lat, Long, 1);

            addressZero = addresses.get(0).getAddressLine(0);
            addressOne = addresses.get(0).getAddressLine(1);
            addressTwo = addresses.get(0).getAddressLine(2);
            Country = addresses.get(0).getCountryName();
            FeatureName = addresses.get(0).getFeatureName();
            AdminArea = addresses.get(0).getAdminArea();
            CountryCode = addresses.get(0).getCountryCode();
            Locality = addresses.get(0).getLocality();
            postalCode = addresses.get(0).getPostalCode();
            subAdminArea = addresses.get(0).getSubAdminArea();
            subLocality = addresses.get(0).getSubLocality();

            city_geo = Locality;
            state_geo = AdminArea;
            country_geo = Country;

            pincode.setText(postalCode);
            city.setText(subLocality + "," + city_geo);
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

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1500;
        window.setAttributes(lp);

        dialog.show();

    }
}
