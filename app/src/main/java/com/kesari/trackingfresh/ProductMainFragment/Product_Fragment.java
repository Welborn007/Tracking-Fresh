package com.kesari.trackingfresh.ProductMainFragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.NearestVehicleMainPOJO;
import com.kesari.trackingfresh.Map.HttpConnection;
import com.kesari.trackingfresh.Map.PathJSONParser;
import com.kesari.trackingfresh.ProductSubFragment.Product_categoryFragment;
import com.kesari.trackingfresh.ProductSubFragment.SubProductMainPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.OnSwipeTouchListener;
import com.kesari.trackingfresh.Utilities.RecyclerItemClickListener;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kesari on 11/04/17.
 */

public class Product_Fragment extends Fragment implements OnMapReadyCallback {

    private LatLng Current_Origin;

    //GoogleMap googleMap;
    private String TAG = this.getClass().getSimpleName();
    //private GPSTracker gps;
    private Location Current_Location;
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME = "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    GridView gridView;
    private LinearLayoutManager llm;

    //private RecyclerView.Adapter adapterItineraryDetails;
    //private MyDataAdapter myDataAdapter;

    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    /*private MarkerOptions currentPositionMarker = null;
    private Marker currentLocationMarker;*/
    ImageView fruits, vegetables, groceries;
    View f1;
    FrameLayout fragment_data;
    Marker marker;

    LatLng oldLocation, newLocation;
    LatLng latlang;

    boolean isMarkerRotating = false;

    SearchView searchView;
    PlacesAutocompleteTextView placesAutocompleteTextView;
    TextView kilometre, GuestAddress,ETA;
    public static RelativeLayout map_Holder;
    LinearLayout layout_holder,product_holder;
    FloatingActionButton fab;
    FancyButton product_category;

    public static FrameLayout frameLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Product_RecyclerAdapter product_recyclerAdapter;

    private Gson gson;
    private ProductCategoryMainPojo productCategoryMainPojo;
    private SubProductMainPOJO subProductMainPOJO;

    private static final int DURATION = 3000;
    ScheduledExecutorService scheduleTaskExecutor;

    NearestVehicleMainPOJO nearestVehicleMainPOJO;
    String[] geoArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product, container, false);

        try
        {

            fruits = (ImageView) V.findViewById(R.id.fruits);
            vegetables = (ImageView) V.findViewById(R.id.vegetables);
            groceries = (ImageView) V.findViewById(R.id.groceries);
            f1 = (View) V.findViewById(R.id.map_container);
            fragment_data = (FrameLayout) V.findViewById(R.id.fragment_data);

            gson = new Gson();

            recyclerView = (RecyclerView) V.findViewById(R.id.product_category_recyclerview);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            searchView = (SearchView) V.findViewById(R.id.searchLocation);
            placesAutocompleteTextView = (PlacesAutocompleteTextView) V.findViewById(R.id.places_autocomplete);

            kilometre = (TextView) V.findViewById(R.id.kilometre);
            ETA = (TextView) V.findViewById(R.id.ETA);
            GuestAddress = (TextView) V.findViewById(R.id.GuestAddress);
            map_Holder = (RelativeLayout) V.findViewById(R.id.map_Holder);
            layout_holder = (LinearLayout) V.findViewById(R.id.layout_holder);
            product_holder = (LinearLayout) V.findViewById(R.id.product_holder);
            fab = (FloatingActionButton) V.findViewById(R.id.fab);
            product_category = (FancyButton) V.findViewById(R.id.product_category);

            frameLayout = (FrameLayout) V.findViewById(R.id.fragment_data);

            /*product_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(product_holder.getVisibility() == View.VISIBLE)
                    {
                        product_holder.setVisibility(View.GONE);
                    }
                    else
                    {
                        product_holder.setVisibility(View.VISIBLE);
                    }
                }
            });*/

            product_category.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    product_category.setIconResource(getString(R.string.drop_down));

                    product_holder.setVisibility(View.VISIBLE);
                    product_holder.startAnimation(slide_up);
                }

                public void onSwipeBottom() {
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    product_category.setIconResource(getString(R.string.drop_up));

                    product_holder.setVisibility(View.GONE);
                    product_holder.startAnimation(slide_down);
                }

            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    if(layout_holder.getVisibility() == View.VISIBLE)
                    {
                        layout_holder.setVisibility(View.GONE);
                        fab.setImageResource(R.drawable.ic_plus);
                        //IOUtils.slideToBottom(layout_holder);
                        layout_holder.startAnimation(slide_down);
                    }
                    else if(layout_holder.getVisibility() == View.GONE)
                    {
                        layout_holder.setVisibility(View.VISIBLE);
                        fab.setImageResource(R.drawable.ic_minus);
                        //IOUtils.slideToTop(layout_holder);
                        layout_holder.startAnimation(slide_up);
                    }

                }
            });

            GuestAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {

                            frameLayout.setVisibility(View.VISIBLE);

                            Product_categoryFragment product_categoryFragment = new Product_categoryFragment();

                            Bundle args = new Bundle();
                            args.putString("category_id",  productCategoryMainPojo.getData().get(position).get_id());
                            product_categoryFragment .setArguments(args);

                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.fragment_data, product_categoryFragment);
                            transaction.commit();

                            map_Holder.setVisibility(View.GONE);

                        }
                    })
            );

        /*placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place

                        Geocoder coder = new Geocoder(getActivity());
                        try {
                            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(place.description, 50);
                            for (Address add : adresses) {

                                double longitude = add.getLongitude();
                                double latitude = add.getLatitude();

                                Current_Origin = new LatLng(latitude, longitude);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );*/

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return V;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try
        {

            mContext = getActivity();

            FragmentManager fm = getActivity().getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            Current_Location = SharedPrefUtil.getLocation(getActivity());
            Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

            Log.i("latitude", String.valueOf(Current_Location.getLatitude()));
            Log.i("longitude", String.valueOf(Current_Location.getLongitude()));

            oldLocation = Current_Origin;


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
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);

            if (!NetworkUtils.isNetworkConnectionOn(getActivity())) {
                FireToast.customSnackbarWithListner(getActivity(), "No internet access", "Settings", new ActionClickListener() {
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

                        Current_Location = SharedPrefUtil.getLocation(getActivity());
                        Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

                        Log.i("laittudeText",String.valueOf(Current_Origin.latitude));

                        if(!String.valueOf(Current_Origin.latitude).equalsIgnoreCase("0.0")  && !String.valueOf(Current_Origin.longitude).equalsIgnoreCase("0.0"))
                        {

                            String url = Constants.CheckNearestVehicle ;

                            Log.i("url", url);

                            JSONObject jsonObject = new JSONObject();

                            try {

                                JSONObject postObject = new JSONObject();

                                postObject.put("longitude", SharedPrefUtil.getLocation(getActivity()).getLongitude());
                                postObject.put("latitude", SharedPrefUtil.getLocation(getActivity()).getLatitude());

                                jsonObject.put("post", postObject);

                                Log.i("JSON CREATED", jsonObject.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

                            IOUtils ioUtils = new IOUtils();

                            ioUtils.sendJSONObjectRequestHeader(getActivity(), url,params, jsonObject, new IOUtils.VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    DriverLocationResponse(result);
                                }
                            });
                        }

                    }
                }, 0, 15, TimeUnit.SECONDS);
            }


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


    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try
        {

            String waypoints = "waypoints=optimize:true|"
                    + Current_Origin.latitude + "," + Current_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints  + "&" + key + "&" + sensor;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + "origin=" + Current_Origin.latitude + "," + Current_Origin.longitude + "&destination=" + destLatitude + ","
                    + destLongitude + "&" + params;

            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);

        } catch (Exception e) {
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
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_delivery_van))
                        //.rotation((float) bearingBetweenLocations(oldLocation,newLocation))
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

                map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
                        .title("Origin"));

            }

            oldLocation = newLocation;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }


    public void getProductDataListing() {
        try {

            IOUtils ioUtils = new IOUtils();

            Log.i("Token",SharedPrefUtil.getToken(getActivity()));

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            String URL = Constants.Product_Category + SharedPrefUtil.getNearestVehicle(getActivity()).getData().get(0).getVehicle_id();

            ioUtils.getGETStringRequestHeader(getActivity(),URL, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("product_category",result);

                    ProductCategoryResponse(result);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ProductCategoryResponse(String Response)
    {

        try
        {
            productCategoryMainPojo = gson.fromJson(Response,ProductCategoryMainPojo.class);

            product_recyclerAdapter = new Product_RecyclerAdapter(productCategoryMainPojo.getData(), getActivity());
            recyclerView.setAdapter(product_recyclerAdapter);
            product_recyclerAdapter.notifyDataSetChanged();

        }catch (Exception e)
        {
            Log.i("exception",e.toString());
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
                //kilometre.setText(distance);

                Log.i("time", String.valueOf(duration));
                ETA.setText("Duration : " + duration + " away");

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
                    polyLineOptions.width(12);

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    polyLineOptions.color(Color.BLUE);
                }

                map.addPolyline(polyLineOptions);

                //scheduleTaskExecutor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void DriverLocationResponse(String resp) {
        map.clear();
        try {

            nearestVehicleMainPOJO = gson.fromJson(resp, NearestVehicleMainPOJO.class);

            if(!nearestVehicleMainPOJO.getData().isEmpty())
            {
                SharedPrefUtil.setNearestVehicle(getActivity(),resp);
                getProductDataListing();
                geoArray = nearestVehicleMainPOJO.getData().get(0).getGeo().getCoordinates();

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
                        tilt(60).
                        zoom(18).
                        bearing(0).
                        build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                newLocation = currentPosition;

                addMarkers("1", "TKF Vehicle", cust_latitude, cust_longitude);
                getMapsApiDirectionsUrl(cust_latitude, cust_longitude);
            }
            else
            {
                GuestAddress.setText(getCompleteAddressString(Current_Origin.latitude,Current_Origin.longitude));
                kilometre.setText("Vehicle Not Available");
                SharedPrefUtil.setNearestVehicle(getActivity(),"");
                //scheduleTaskExecutor.shutdown();
            }

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

}
