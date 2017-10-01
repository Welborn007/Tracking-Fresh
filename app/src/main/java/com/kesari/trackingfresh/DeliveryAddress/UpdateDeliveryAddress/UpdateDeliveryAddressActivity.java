package com.kesari.trackingfresh.DeliveryAddress.UpdateDeliveryAddress;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.VehicleNearestRoute.NearestRouteMainPOJO;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class UpdateDeliveryAddressActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt,OnMapReadyCallback {

    String _id,fullName,email_Id,mobileNo,flat_No,buildingName,landmark,city,state,address_Type,pincodeTxt;
    boolean isDefault;

    FancyButton confirmAddress;
    EditText name,email,mobile,cityTxt,stateTxt,pincode,flat_no,building_name,landmarkTxt,addressType;
    private String TAG = this.getClass().getSimpleName();
    //private GPSTracker gpsTracker;
    //private Location Current_Origin;
    private NetworkUtilsReceiver networkUtilsReceiver;
    CheckBox defaultAddress;
    private SupportMapFragment supportMapFragment;

    String Latitude,Longitude;
    NestedScrollView nestedScrollView;
    private LatLng Current_Location;
    Marker marker;
    private Gson gson;
    NearestRouteMainPOJO nearestRouteMainPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delivery_address);

        _id = getIntent().getStringExtra("_id");
        fullName = getIntent().getStringExtra("fullName");
        email_Id = getIntent().getStringExtra("email_Id");
        mobileNo = getIntent().getStringExtra("mobileNo");
        flat_No = getIntent().getStringExtra("flat_No");
        buildingName = getIntent().getStringExtra("buildingName");
        landmark = getIntent().getStringExtra("landmark");
        city = getIntent().getStringExtra("city");
        state = getIntent().getStringExtra("state");
        address_Type = getIntent().getStringExtra("address_Type");
        Latitude = getIntent().getStringExtra("latitude");
        Longitude = getIntent().getStringExtra("longitude");
        pincodeTxt = getIntent().getStringExtra("pincode");
        isDefault = getIntent().getBooleanExtra("isDefault",true);

        sendLATLONVehicle(Latitude,Longitude);

        gson = new Gson();

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
                IOUtils.buildAlertMessageNoGps(UpdateDeliveryAddressActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            FragmentManager fm = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container_address);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            confirmAddress = (FancyButton) findViewById(R.id.confirmAddress);

            name = (EditText) findViewById(R.id.name);
            email = (EditText) findViewById(R.id.email);
            mobile = (EditText) findViewById(R.id.mobile);

            cityTxt = (EditText) findViewById(R.id.city);
            stateTxt = (EditText) findViewById(R.id.state);
            pincode = (EditText) findViewById(R.id.pincode);

            flat_no = (EditText) findViewById(R.id.flat_no);
            building_name = (EditText) findViewById(R.id.building_name);
            landmarkTxt = (EditText) findViewById(R.id.landmark);
            addressType = (EditText) findViewById(R.id.addressType);
            defaultAddress = (CheckBox) findViewById(R.id.defaultAddress);
            nestedScrollView = (NestedScrollView) findViewById(R.id.mapScroll);

            ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

            transparentImageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            nestedScrollView.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            nestedScrollView.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            nestedScrollView.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
            });

            try
            {
                name.setText(fullName);
                email.setText(email_Id);
                mobile.setText(mobileNo);
                cityTxt.setText(city);
                stateTxt.setText(state);
                pincode.setText(pincodeTxt);
                flat_no.setText(flat_No);
                building_name.setText(buildingName);
                landmarkTxt.setText(landmark);
                addressType.setText(address_Type);

                if(isDefault)
                {
                    defaultAddress.setChecked(true);
                }
                else
                {
                    defaultAddress.setChecked(false);
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

            confirmAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String FullName = name.getText().toString().trim();
                    String EmailID = email.getText().toString().trim();
                    String MobileNum = mobile.getText().toString().trim();
                    String FlatNum = flat_no.getText().toString().trim();
                    String BuildingName = building_name.getText().toString().trim();
                    String Landmark = landmarkTxt.getText().toString().trim();
                    String City = cityTxt.getText().toString().trim();
                    String State = stateTxt.getText().toString().trim();
                    String Pincode = pincode.getText().toString().trim();
                    String AddressType = addressType.getText().toString().trim();
                    String DefaultAddress = "false";

                    if(defaultAddress.isChecked())
                    {
                        DefaultAddress = "true";
                    }
                    else
                    {
                        DefaultAddress = "false";
                    }

                    if(!FullName.isEmpty() && !EmailID.isEmpty() && !MobileNum.isEmpty() && !FlatNum.isEmpty() && !BuildingName.isEmpty() && !Landmark.isEmpty() && !City.isEmpty() && !State.isEmpty() && !Pincode.isEmpty() && !AddressType.isEmpty() && !Latitude.isEmpty() && !Longitude.isEmpty())
                    {
                        UpdateAddress(_id,FullName,EmailID,MobileNum,FlatNum,BuildingName,Landmark,City,State,Pincode,AddressType,DefaultAddress);
                    }
                    else if(FullName.isEmpty())
                    {
                        name.setError(getString(R.string.FullName));
                    }
                    else if(EmailID.isEmpty())
                    {
                        email.setError(getString(R.string.email_id));
                    }
                    else if(MobileNum.isEmpty())
                    {
                        mobile.setError(getString(R.string.mobileno));
                    }
                    else if(FlatNum.isEmpty())
                    {
                        flat_no.setError(getString(R.string.flatno));
                    }
                    else if(BuildingName.isEmpty())
                    {
                        building_name.setError(getString(R.string.buildingName));
                    }
                    else if(Landmark.isEmpty())
                    {
                        landmarkTxt.setError(getString(R.string.Landmark));
                    }
                    else if(City.isEmpty())
                    {
                        cityTxt.setError(getString(R.string.City));
                    }
                    else if(State.isEmpty())
                    {
                        stateTxt.setError(getString(R.string.State));
                    }
                    else if(Pincode.isEmpty())
                    {
                        pincode.setError(getString(R.string.PinCode));
                    }
                    else if(AddressType.isEmpty())
                    {
                        addressType.setError(getString(R.string.addressType));
                    }
                    else if(Latitude.isEmpty() || Longitude.isEmpty())
                    {
                        //Toast.makeText(UpdateDeliveryAddressActivity.this, "Please Set Your Location On Map!!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(UpdateDeliveryAddressActivity.this)
                                .setTitleText("Please Set Your Location On Map!!")
                                .show();
                    }

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        try
        {

            if (ActivityCompat.checkSelfPermission(UpdateDeliveryAddressActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(UpdateDeliveryAddressActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    Latitude = String.valueOf(latLng.latitude);
                    Longitude = String.valueOf(latLng.longitude);
                    sendLATLONVehicle(Latitude,Longitude);

                    marker.setPosition(latLng);
                }
            });

            //Current_Origin = SharedPrefUtil.getLocation(UpdateDeliveryAddressActivity.this);
            Current_Location = new LatLng(Double.parseDouble(Latitude),Double.parseDouble(Longitude));

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(Current_Location).
                    zoom(15).
                    build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                    Latitude = String.valueOf(marker.getPosition().latitude);
                    Longitude = String.valueOf(marker.getPosition().longitude);
                    sendLATLONVehicle(Latitude,Longitude);
                }
            });


            marker = googleMap.addMarker(new MarkerOptions().position(Current_Location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker_hi)).draggable(true));

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void sendLATLONVehicle(String Latitude,String Longitude)
    {
        try
        {

            String url = Constants.VehicleNearestRoute ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("longitude", Longitude);
                postObject.put("latitude", Latitude);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(UpdateDeliveryAddressActivity.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(UpdateDeliveryAddressActivity.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    NearestVehicleResponse(result);
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

    private void NearestVehicleResponse(String Response)
    {
        try
        {
            nearestRouteMainPOJO = gson.fromJson(Response, NearestRouteMainPOJO.class);

            if(nearestRouteMainPOJO.getData().isEmpty())
            {
                final Dialog dialog = new Dialog(UpdateDeliveryAddressActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_unavailable_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                FancyButton btnCancel = (FancyButton) dialog.findViewById(R.id.btnCancel);
                btnCancel.setText("Sorry! We don't serve on Current Location. Please set another Location on Map!!");
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Latitude = "";
                Longitude = "";
            }
            else
            {
                //Toast.makeText(UpdateDeliveryAddressActivity.this, "Location Set!!", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(UpdateDeliveryAddressActivity.this)
                        .setTitleText("Location Set!!")
                        .show();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void UpdateAddress(String addressID,String fullName,String emailId,String mobileNo,String flat_no,String buildingName,String landmark,String city,String state,String pincode,String address_Type,String isDefault)
    {
        try
        {

            String url = Constants.UpdateAddress ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();
                postObject.put("id", addressID);
                postObject.put("fullName", fullName);
                postObject.put("email_Id", emailId);
                postObject.put("mobileNo", mobileNo);
                postObject.put("flat_No", flat_no);
                postObject.put("buildingName", buildingName);
                postObject.put("landmark", landmark);
                postObject.put("city",city );
                postObject.put("state", state);
                postObject.put("pincode", pincode);
                postObject.put("address_Type", address_Type);
                postObject.put("isDefault", isDefault);
                postObject.put("latitude", Latitude);
                postObject.put("longitude",Longitude);


                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(UpdateDeliveryAddressActivity.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectPutRequestHeader(UpdateDeliveryAddressActivity.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("AddressUpdated",result);

                    AddAddressResponse(result);
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

    private void AddAddressResponse(String Response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(Message.equalsIgnoreCase("Updated Successfully"))
            {
                finish();
            }

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
