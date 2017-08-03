package com.kesari.trackingfresh.OrderTracking;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.NearestVehicleMainPOJO;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.Map.HttpConnection;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.Map.PathJSONParser;
import com.kesari.trackingfresh.Order.OrderReview;
import com.kesari.trackingfresh.Order.OrderReviewMainPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

public class OrderBikerTrackingActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt,OnMapReadyCallback {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private SupportMapFragment supportMapFragment;
    private LatLng Current_Origin,Delivery_Origin;
    //GoogleMap googleMap;
    private String TAG = this.getClass().getSimpleName();
    //private GPSTracker gps;
    private Location Current_Location;
    LatLng oldLocation, newLocation;
    private static final int DURATION = 3000;
    ScheduledExecutorService scheduleTaskExecutor;
    private GoogleMap map;
    Marker marker;
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME = "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    TextView kilometre, GuestAddress,ETA;
    FancyButton btnSubmit;
    private Gson gson;
    NearestVehicleMainPOJO nearestVehicleMainPOJO;
    String[] geoArray;
    String OrderID;
    OrderReviewMainPOJO orderReviewMainPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_biker_tracking);

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
                IOUtils.buildAlertMessageNoGps(OrderBikerTrackingActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            kilometre = (TextView) findViewById(R.id.kilometre);
            ETA = (TextView) findViewById(R.id.ETA);
            GuestAddress = (TextView) findViewById(R.id.GuestAddress);

            btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);

            OrderID = getIntent().getStringExtra("orderID");
            getOrderDetailsfromID();
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderBikerTrackingActivity.this, OrderReview.class);
                    intent.putExtra("orderID",OrderID);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        try
        {

            FragmentManager fm = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            Current_Location = SharedPrefUtil.getLocation(OrderBikerTrackingActivity.this);

            Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

            Log.i("latitude", String.valueOf(Current_Location.getLatitude()));
            Log.i("longitude", String.valueOf(Current_Location.getLongitude()));



        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getOrderDetailsfromID() {
        try {

            String url = Constants.OrderDetails + OrderID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OrderBikerTrackingActivity.this));

            ioUtils.getGETStringRequestHeader(OrderBikerTrackingActivity.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    OrderDetailsResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void OrderDetailsResponse(String Response)
    {
        try
        {
            orderReviewMainPOJO = gson.fromJson(Response, OrderReviewMainPOJO.class);

            Delivery_Origin = new LatLng(Double.parseDouble(orderReviewMainPOJO.getData().getAddress().getLatitude()), Double.parseDouble(orderReviewMainPOJO.getData().getAddress().getLongitude()));
            oldLocation = Delivery_Origin;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try
        {

            //getData();

            map = googleMap;
            if (ActivityCompat.checkSelfPermission(OrderBikerTrackingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(OrderBikerTrackingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            map.setMyLocationEnabled(true);

            if (!NetworkUtils.isNetworkConnectionOn(OrderBikerTrackingActivity.this)) {
                FireToast.customSnackbarWithListner(OrderBikerTrackingActivity.this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }
            else
            {
                scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

                // This schedule a task to run every 10 minutes:
                scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {

                        Current_Location = SharedPrefUtil.getLocation(OrderBikerTrackingActivity.this);
                        Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

                        Log.i("laittudeText",String.valueOf(Current_Origin.latitude));

                        String url = Constants.CheckNearestVehicle ;

                        Log.i("url", url);

                        JSONObject jsonObject = new JSONObject();

                        try {

                            JSONObject postObject = new JSONObject();

                            postObject.put("longitude", SharedPrefUtil.getLocation(OrderBikerTrackingActivity.this).getLongitude());
                            postObject.put("latitude", SharedPrefUtil.getLocation(OrderBikerTrackingActivity.this).getLatitude());

                            jsonObject.put("post", postObject);

                            Log.i("JSON CREATED", jsonObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OrderBikerTrackingActivity.this));

                        IOUtils ioUtils = new IOUtils();

                        ioUtils.sendJSONObjectRequestHeader(OrderBikerTrackingActivity.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                DriverLocationResponse(result);
                            }
                        });
                    }
                }, 0, 3, TimeUnit.SECONDS);
            }

        /*ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a task to run every 10 minutes:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (!NetworkUtils.isNetworkConnectionOn(getActivity())) {
                    FireToast.customSnackbarWithListner(getActivity(), "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;
                } else {
                    //getDriverLocationTask();

                    IOUtils ioUtils = new IOUtils();

                    ioUtils.getGETStringRequest(getActivity(),Constants.LocationAPI, new IOUtils.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            DriverLocationResponse(result);
                        }
                    });


                    //DriverLocationResponse(IOUtils.getStringRequest(getActivity(),Constants.LocationAPI));
                    //getDriverLocationTaskSample();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);*/

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    return false;
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    public void DriverLocationResponse(String resp) {
        map.clear();
        try {

            nearestVehicleMainPOJO = gson.fromJson(resp, NearestVehicleMainPOJO.class);

            if(nearestVehicleMainPOJO.getData().isEmpty())
            {
                Intent intent = new Intent(OrderBikerTrackingActivity.this, CheckVehicleActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                geoArray = nearestVehicleMainPOJO.getData().get(0).getGeo().getCoordinates();
            }

            Double cust_longitude = Double.parseDouble(geoArray[0]);
            Double cust_latitude = Double.parseDouble(geoArray[1]);

//                final LatLng startPosition = marker.getPosition();
            final LatLng finalPosition = new LatLng(cust_latitude, cust_longitude);

            LatLng currentPosition = new LatLng(
                    cust_latitude,
                    cust_longitude);

            //marker.setPosition(currentPosition);

            map.setTrafficEnabled(true);

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(finalPosition).
                    tilt(0).
                    zoom(16).
                    bearing(0).
                    build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            newLocation = currentPosition;

            addMarkers("1", "TKF Vehicle", cust_latitude, cust_longitude);
            getMapsApiDirectionsUrl(cust_latitude, cust_longitude);

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    private void addMarkers(String id, String location_name, Double latitude, Double longitude) {

        try
        {

            final LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_up_angle))
                        .rotation((float) bearingBetweenLocations(oldLocation,newLocation))
                        .title(location_name));

                data.put(TAG_ID, id);
                data.put(TAG_LOCATION_NAME, location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                extraMarkerInfo.put(marker.getId(), data);

                IOUtils.showRipples(dest,map,DURATION);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IOUtils.showRipples(dest,map,DURATION);
                    }
                }, DURATION - 500);

                map.addMarker(new MarkerOptions().position(Delivery_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer))
                        .title("Origin"));

                //float bearing = (float) bearing(convertLatLngToLocation(oldLocation),convertLatLngToLocation(newLocation))
                //rotateMarker(marker, bearing);
            }

            oldLocation = newLocation;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try
        {

            String waypoints = "waypoints=optimize:true|"
                    + Delivery_Origin.latitude + "," + Delivery_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints  + "&" + key + "&" + sensor;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + "origin=" + Delivery_Origin.latitude + "," + Delivery_Origin.longitude + "&destination=" + destLatitude + ","
                    + destLongitude + "&" + params;

            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                //data = http.readUrl("https://maps.googleapis.com/maps/api/directions/json?origin=17.449797,78.373037&destination=17.47989,78.390095&%20waypoints=optimize:true|17.449797,78.373037||17.47989,78.390095&sensor=false");
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("ReadTaskResult", result);

            String distance = "";
            String duration = "";

            try {

                JSONObject jsonObjectMain = new JSONObject(result);

                JSONArray jsonArray = jsonObjectMain.getJSONArray("routes");

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONArray legs = jsonObject.getJSONArray("legs");

                JSONObject jsonObject1 = legs.getJSONObject(1);

                JSONObject jsonObject2 = jsonObject1.getJSONObject("distance");
                JSONObject jsonObject3 = jsonObject1.getJSONObject("duration");

                distance = jsonObject2.getString("text");
                duration = jsonObject3.getString("text");

                Log.i("Distance", String.valueOf(distance));
                kilometre.setText(distance);

                Log.i("time", String.valueOf(duration));
                ETA.setText("Estimated Delivery Time: " + duration);

                String EndAddress = jsonObject1.getString("end_address");
                kilometre.setText("Vehicle is " + distance + " away at " + EndAddress);

                String StartAddress = jsonObject1.getString("start_address");
                GuestAddress.setText(/*"Your Address: " +*/ StartAddress);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("steps");
                JSONObject jsonObject4 = jsonArray1.getJSONObject(jsonArray1.length()-1);

                String Instructions = jsonObject4.getString("html_instructions");

                try {
                    // Convert from Unicode to UTF-8
                    //String string = "abc\u5639\u563b";
                    byte[] utf8 = Instructions.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    Instructions = new String(utf8, "UTF-8");

                } catch (UnsupportedEncodingException e) {}

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i("Direction",String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                    //kilometre.setText(String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                }
                else
                {
                    Log.i("Direction",String.valueOf(Html.fromHtml(Instructions)));
                    //kilometre.setText(String.valueOf(Html.fromHtml(Instructions)));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            try {
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    polyLineOptions.color(Color.BLUE);
                }

                map.addPolyline(polyLineOptions);

            } catch (Exception e) {
                e.printStackTrace();
            }
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

            if(!scheduleTaskExecutor.isShutdown())
            {
                scheduleTaskExecutor.shutdown();
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(OrderBikerTrackingActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(OrderBikerTrackingActivity.this, DashboardActivity.class);
                startActivity(intent);
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
