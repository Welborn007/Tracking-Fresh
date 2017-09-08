package com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

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
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.Default_DeliveryAddress;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.VehicleNearestRoute.NearestRouteMainPOJO;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class Add_DeliveryAddress extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt,OnMapReadyCallback
{

    FancyButton confirmAddress;
    EditText name,email,mobile,city,state,pincode,flat_no,building_name,landmark,addressType;
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
    CheckBox defaultAddress;
    private Gson gson;
    private AddAddressPOJO addAddressPOJO;
    private SupportMapFragment supportMapFragment;

    String Latitude,Longitude;
    NestedScrollView nestedScrollView;
    private LatLng Current_Location;
    Marker marker;
    NearestRouteMainPOJO nearestRouteMainPOJO;

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

            gson = new Gson();

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

            city = (EditText) findViewById(R.id.city);
            state = (EditText) findViewById(R.id.state);
            pincode = (EditText) findViewById(R.id.pincode);

            flat_no = (EditText) findViewById(R.id.flat_no);
            building_name = (EditText) findViewById(R.id.building_name);
            landmark = (EditText) findViewById(R.id.landmark);
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

            //gpsTracker = new GPSTracker(Add_DeliveryAddress.this);

            Current_Origin = SharedPrefUtil.getLocation(Add_DeliveryAddress.this);

            Double Lat = Current_Origin.getLatitude();
            Double Long = Current_Origin.getLongitude();

            FullName = SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getLastName();

            name.setText(FullName);
            email.setText(SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getEmailId());
            mobile.setText(SharedPrefUtil.getUser(Add_DeliveryAddress.this).getData().getMobileNo());

            Latitude = String.valueOf(SharedPrefUtil.getLocation(Add_DeliveryAddress.this).getLatitude());
            Longitude = String.valueOf(SharedPrefUtil.getLocation(Add_DeliveryAddress.this).getLongitude());

            sendLATLONVehicle(Latitude,Longitude);

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

                    String FullName = name.getText().toString().trim();
                    String EmailID = email.getText().toString().trim();
                    String MobileNum = mobile.getText().toString().trim();
                    String FlatNum = flat_no.getText().toString().trim();
                    String BuildingName = building_name.getText().toString().trim();
                    String Landmark = landmark.getText().toString().trim();
                    String City = city.getText().toString().trim();
                    String State = state.getText().toString().trim();
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
                        AddNewAddress(FullName,EmailID,MobileNum,FlatNum,BuildingName,Landmark,City,State,Pincode,AddressType,DefaultAddress);
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
                        landmark.setError(getString(R.string.Landmark));
                    }
                    else if(City.isEmpty())
                    {
                        city.setError(getString(R.string.City));
                    }
                    else if(State.isEmpty())
                    {
                        state.setError(getString(R.string.State));
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
                        Toast.makeText(Add_DeliveryAddress.this, "Please Set Your Location On Map!!", Toast.LENGTH_SHORT).show();
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

            if (ActivityCompat.checkSelfPermission(Add_DeliveryAddress.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(Add_DeliveryAddress.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            Current_Origin = SharedPrefUtil.getLocation(Add_DeliveryAddress.this);
            Current_Location = new LatLng(Current_Origin.getLatitude(), Current_Origin.getLongitude());

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


            marker = googleMap.addMarker(new MarkerOptions().position(Current_Location).icon(BitmapDescriptorFactory .fromResource(R.drawable.ic_location_marker_hi)).draggable(true));

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
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(Add_DeliveryAddress.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(Add_DeliveryAddress.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    NearestVehicleResponse(result);
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
                final Dialog dialog = new Dialog(Add_DeliveryAddress.this);
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
                Toast.makeText(Add_DeliveryAddress.this, "Location Set!!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void AddNewAddress(String fullName,String emailId,String mobileNo,String flat_no,String buildingName,String landmark,String city,String state,String pincode,String address_Type,String isDefault)
    {
        try
        {

            String url = Constants.NewAddress ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

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
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(Add_DeliveryAddress.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(Add_DeliveryAddress.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("AddressAdded",result);

                    AddAddressResponse(result);
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

            addAddressPOJO = gson.fromJson(Response,AddAddressPOJO.class);

            if(getIntent().getStringExtra("value") != null || getIntent().getStringExtra("SettingAddress") != null)
            {
                finish();
            }
            else
            {
                if(!addAddressPOJO.getAddress().get_id().isEmpty())
                {
                    Intent intent = new Intent(Add_DeliveryAddress.this,Default_DeliveryAddress.class);
                    intent.putExtra("FullName",FullName);
                    intent.putExtra("city",subLocality + " , " + city_geo);
                    intent.putExtra("postalCode",postalCode);
                    startActivity(intent);
                    finish();
                }
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
