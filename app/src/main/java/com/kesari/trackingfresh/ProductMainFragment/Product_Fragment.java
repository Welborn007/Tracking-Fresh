package com.kesari.trackingfresh.ProductMainFragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
import com.kesari.trackingfresh.Map.HttpConnection;
import com.kesari.trackingfresh.Map.JSON_POJO;
import com.kesari.trackingfresh.Map.PathJSONParser;
import com.kesari.trackingfresh.ProductSubFragment.Product_categoryFragment;
import com.kesari.trackingfresh.ProductSubFragment.SubProductMainPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.OnSwipeTouchListener;
import com.kesari.trackingfresh.Utilities.RecyclerItemClickListener;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.VehicleNearestRoute.NearestRouteMainPOJO;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mehdi.sakout.fancybuttons.FancyButton;
import pl.droidsonroids.gif.GifImageView;

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
    View f1;
    FrameLayout fragment_data;
    Marker marker;

    LatLng oldLocation, newLocation;
    LatLng latlang;

    boolean isMarkerRotating = false;

    SearchView searchView;
    PlacesAutocompleteTextView placesAutocompleteTextView;
    TextView kilometre, GuestAddress, ETA;
    public static RelativeLayout map_Holder;
    LinearLayout layout_holder, product_holder;
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

    // NearestVehicleMainPOJO nearestVehicleMainPOJO;
    NearestRouteMainPOJO nearestRouteMainPOJO;
    SocketLiveMainPOJO scoketLiveMainPOJO;
    String[] geoArray;

    boolean isVehiclePresent = true;

    private Socket socket;
    GifImageView arrow_down, arrow_up;
    Bitmap bitmap;

    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    private LatLng Old_Origin;
    //Marker markerVehicle;
    private static final String TAG_FROM_TIME = "";
    private static final String TAG_TO_TIME = "";
    Marker markerVehicle, Cust_Marker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product, container, false);

        try {

            f1 = (View) V.findViewById(R.id.map_container);
            fragment_data = (FrameLayout) V.findViewById(R.id.fragment_data);

            arrow_down = (GifImageView) V.findViewById(R.id.arrow_down);
            arrow_up = (GifImageView) V.findViewById(R.id.arrow_up);

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
            product_category = (FancyButton) V.findViewById(R.id.product_category);

            frameLayout = (FrameLayout) V.findViewById(R.id.fragment_data);

            product_category.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    //product_category.setIconResource(getString(R.string.drop_down));
                    arrow_down.setVisibility(View.VISIBLE);
                    arrow_up.setVisibility(View.GONE);

                    product_holder.setVisibility(View.VISIBLE);
                    product_holder.startAnimation(slide_up);
                }

                public void onSwipeBottom() {
                    Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_down);

                    Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slide_up);

                    arrow_down.setVisibility(View.GONE);
                    arrow_up.setVisibility(View.VISIBLE);
                    //recyclerView.setAdapter(null);

                    //product_category.setIconResource(getString(R.string.drop_up));

                    product_holder.setVisibility(View.GONE);
                    product_holder.startAnimation(slide_down);
                }

            });


            GuestAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //sendNotification("Hello World","https://d3r8gwkgo0io6y.cloudfront.net/upload/assets/Kesari-Tours.png");
                        }
                    });
                }
            });

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            frameLayout.setVisibility(View.VISIBLE);

                            Product_categoryFragment product_categoryFragment = new Product_categoryFragment();

                            Bundle args = new Bundle();
                            args.putString("category_id", productCategoryMainPojo.getData().get(position).get_id());
                            product_categoryFragment.setArguments(args);

                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.fragment_data, product_categoryFragment);
                            transaction.commit();

                            map_Holder.setVisibility(View.GONE);

                        }
                    })
            );

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return V;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {

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

        try {

            //getData();

            map = googleMap;
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);

            if (!NetworkUtils.isNetworkConnectionOn(getActivity())) {
                /*FireToast.customSnackbarWithListner(getActivity(), "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;*/

                new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
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
            } else {
                startSocket();
                setVehicleEmpty();

                scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

                // This schedule a task to run every 10 minutes:
                scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        getVehicleLocation();
                    }
                }, 0, 60, TimeUnit.SECONDS);

            }

            getVehicleRoute(SharedPrefUtil.getNearestRouteMainPOJO(getActivity()).getData().get(0).getVehicleId());

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {


                    return false;
                }
            });

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View myContentView = getActivity().getLayoutInflater().inflate(R.layout.map_infolayout, null);

                    try {
                        // Get extra data with marker ID
                        HashMap<String, String> marker_data = extraMarkerInfo.get(marker.getId());
                        TextView mapLoc = ((TextView) myContentView.findViewById(R.id.mapLoc));
                        TextView from_time = ((TextView) myContentView.findViewById(R.id.from_time));
                        TextView to_time = ((TextView) myContentView.findViewById(R.id.to_time));
                        View viewLine = ((View) myContentView.findViewById(R.id.viewLine));
                        TextView viewHeader = ((TextView) myContentView.findViewById(R.id.viewHeader));

                        if (marker.getTitle().equalsIgnoreCase("Origin")) {
                            mapLoc.setText(SharedPrefUtil.getUser(getActivity()).getData().getFirstName());
                            from_time.setVisibility(View.GONE);
                            to_time.setVisibility(View.GONE);
                            viewLine.setVisibility(View.GONE);
                            viewHeader.setVisibility(View.GONE);
                        } else if (!marker.getTitle().equalsIgnoreCase("TKF Vehicle")) {
                            // Getting the data from Map
                            String latitude = marker_data.get(TAG_LATITUDE);
                            String longitude = marker_data.get(TAG_LONGITUDE);
                            String place = marker_data.get(TAG_LOCATION_NAME);
                            String id = marker_data.get(TAG_ID);
                            String startTime = marker_data.get(TAG_FROM_TIME);
                            String endTime = marker_data.get(TAG_TO_TIME);

                            from_time.setVisibility(View.VISIBLE);
                            to_time.setVisibility(View.VISIBLE);
                            viewLine.setVisibility(View.VISIBLE);
                            viewHeader.setVisibility(View.VISIBLE);

                            mapLoc.setText(place);
                            from_time.setText(startTime);
                            to_time.setText(endTime);
                        } else if (marker.getTitle().equalsIgnoreCase("TKF Vehicle")) {
                            mapLoc.setText("TKF Vehicle");
                            from_time.setVisibility(View.GONE);
                            to_time.setVisibility(View.GONE);
                            viewLine.setVisibility(View.GONE);
                            viewHeader.setVisibility(View.GONE);
                        }


                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    }

                    return myContentView;
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void getVehicleLocation() {
        String url = Constants.VehicleNearestRoute;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("longitude", String.valueOf(SharedPrefUtil.getLocation(getActivity()).getLongitude()));
            postObject.put("latitude", String.valueOf(SharedPrefUtil.getLocation(getActivity()).getLatitude()));

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeader(getActivity(), url, params, jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                VehicleNearestRouteResponse(result);
            }
        });
    }

    private void VehicleNearestRouteResponse(String Response) {
        try {
            nearestRouteMainPOJO = gson.fromJson(Response, NearestRouteMainPOJO.class);

            if (nearestRouteMainPOJO.getData().isEmpty()) {

                SharedPrefUtil.setNearestRouteMainPOJO(getActivity(), "");
                //setVehicleEmpty();
            } else {
                SharedPrefUtil.setNearestRouteMainPOJO(getActivity(), Response);
                getProductDataListing();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        //SharedPrefUtil.setNearestRouteMainPOJO(getActivity(),Response);

    }

    private void startSocket() {
        try {
            socket = IO.socket(Constants.VehicleLiveLocation);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("hello", "server");
                    obj.put("binary", new byte[42]);
                    socket.emit("vehiclePosition", obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //socket.disconnect();
                Log.i("Send", "Data " + socket.id());
            }

        }).on("vehiclePosition", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                final JSONObject obj = (JSONObject) args[0];
                Log.i("Connect", obj.toString());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DriverSocketLiveLocationResponse(obj.toString());
                    }
                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i("DisConnect", "Connect");
            }

        });
        socket.connect();
    }

    private void stopSocket() {
        socket.disconnect();
        Log.i("SocketService", "Disconnected");
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

        try {

            String waypoints = "waypoints=optimize:true|"
                    + Current_Origin.latitude + "," + Current_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints + "&" + key + "&" + sensor;
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

    public void getRouteMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try {

            String waypoints = "waypoints=optimize:true|"
                    + Old_Origin.latitude + "," + Old_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints + "&" + sensor + "&" + key;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + "origin=" + Old_Origin.latitude + "," + Old_Origin.longitude + "&destination=" + destLatitude + ","
                    + destLongitude + "&" + params;

            RouteReadTask downloadTask = new RouteReadTask();
            downloadTask.execute(url);

            Old_Origin = new LatLng(destLatitude, destLongitude);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void addMarkers(String id, String location_name, Double latitude, Double longitude) {

        try {
            if (marker != null) {
                marker.remove();
            }

            if (Cust_Marker != null) {
                Cust_Marker.remove();
            }

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

                IOUtils.showRipples(dest, map, DURATION);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IOUtils.showRipples(dest, map, DURATION);
                    }
                }, DURATION - 500);

                Cust_Marker = map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer))
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

            Log.i("Token", SharedPrefUtil.getToken(getActivity()));

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            String URL = Constants.Product_Category + SharedPrefUtil.getNearestRouteMainPOJO(getActivity()).getData().get(0).getVehicleId();

            ioUtils.getGETStringRequestHeader(getActivity(), URL, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("product_category", result);

                    ProductCategoryResponse(result);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ProductCategoryResponse(String Response) {

        try {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            //product_category.setIconResource(getString(R.string.drop_down));

            arrow_down.setVisibility(View.VISIBLE);
            arrow_up.setVisibility(View.GONE);

            product_holder.setVisibility(View.VISIBLE);
            //product_holder.startAnimation(slide_up);

            isVehiclePresent = false;

            productCategoryMainPojo = gson.fromJson(Response, ProductCategoryMainPojo.class);

            product_recyclerAdapter = new Product_RecyclerAdapter(productCategoryMainPojo.getData(), getActivity());
            recyclerView.setAdapter(product_recyclerAdapter);
            product_recyclerAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.i("exception", e.toString());
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
                JSONObject jsonObject4 = jsonArray1.getJSONObject(jsonArray1.length() - 1);

                String Instructions = jsonObject4.getString("html_instructions");

                try {
                    // Convert from Unicode to UTF-8
                    //String string = "abc\u5639\u563b";
                    byte[] utf8 = Instructions.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    Instructions = new String(utf8, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i("Direction", String.valueOf(Html.fromHtml(Instructions, Html.FROM_HTML_MODE_LEGACY)));
                    //kilometre.setText(String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                } else {
                    Log.i("Direction", String.valueOf(Html.fromHtml(Instructions)));
                    //kilometre.setText(String.valueOf(Html.fromHtml(Instructions)));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            //new ParserTask().execute(result);
        }
    }

    private class RouteReadTask extends AsyncTask<String, Void, String> {
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
                //ETA.setText("Duration : " + duration + " away");

                String EndAddress = jsonObject1.getString("end_address");
                //kilometre.setText("Vehicle is " + distance + " away at " + EndAddress);

                kilometre.setText("Vehicle Not Available");

                String StartAddress = jsonObject1.getString("start_address");
                GuestAddress.setText(getCompleteAddressString(Current_Origin.latitude, Current_Origin.longitude));

                JSONArray jsonArray1 = jsonObject1.getJSONArray("steps");
                JSONObject jsonObject4 = jsonArray1.getJSONObject(jsonArray1.length() - 1);

                String Instructions = jsonObject4.getString("html_instructions");

                try {
                    // Convert from Unicode to UTF-8
                    //String string = "abc\u5639\u563b";
                    byte[] utf8 = Instructions.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    Instructions = new String(utf8, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i("Direction", String.valueOf(Html.fromHtml(Instructions, Html.FROM_HTML_MODE_LEGACY)));
                    //kilometre.setText(String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                } else {
                    Log.i("Direction", String.valueOf(Html.fromHtml(Instructions)));
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
            strAdd = "Unable to fetch location";
        }
        return strAdd;
    }

    public void DriverSocketLiveLocationResponse(String resp) {
        //map.clear();
        try {

            scoketLiveMainPOJO = gson.fromJson(resp, SocketLiveMainPOJO.class);
            nearestRouteMainPOJO = SharedPrefUtil.getNearestRouteMainPOJO(getActivity());


            if (nearestRouteMainPOJO.getData() != null) {
                if (!nearestRouteMainPOJO.getData().isEmpty()) {

                    if (scoketLiveMainPOJO.getData() != null) {
                        String NearestVehicleRouteID = nearestRouteMainPOJO.getData().get(0).getVehicleId();
                        String SocketVehicleID = scoketLiveMainPOJO.getData().getVehicle_id();

                        if (NearestVehicleRouteID.equalsIgnoreCase(SocketVehicleID)) {
                            SharedPrefUtil.setSocketLiveMainPOJO(getActivity(), resp);

                            geoArray = scoketLiveMainPOJO.getData().getGeo().getCoordinates();

                            Double cust_longitude = Double.parseDouble(geoArray[0]);
                            Double cust_latitude = Double.parseDouble(geoArray[1]);

                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(cust_latitude);
                            location.setLongitude(cust_longitude);

                            if (Current_Location.distanceTo(location) < 5000) {
                                // bingo!
                                if (isVehiclePresent) {

                                }

                                //final LatLng startPosition = marker.getPosition();
                                final LatLng finalPosition = new LatLng(cust_latitude, cust_longitude);

                                LatLng currentPosition = new LatLng(
                                        cust_latitude,
                                        cust_longitude);

                                //marker.setPosition(currentPosition);

                                map.setTrafficEnabled(true);

                                CameraPosition cameraPosition = new CameraPosition.Builder().
                                        target(finalPosition).
                                        tilt(60).
                                        zoom(14).
                                        bearing(0).
                                        build();

                                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                newLocation = currentPosition;

                                addMarkers("1", "TKF Vehicle", cust_latitude, cust_longitude);
                                getMapsApiDirectionsUrl(cust_latitude, cust_longitude);
                            } else {
                                //setVehicleEmpty();
                            }
                        } else {
                            //setVehicleEmpty();
                        }
                    } else {
                        //setVehicleEmpty();
                    }

                } else {
                    //setVehicleEmpty();
                }
            } else {
                //setVehicleEmpty();
            }

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    private void setVehicleEmpty() {
        isVehiclePresent = true;
        Current_Location = SharedPrefUtil.getLocation(getActivity());
        Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());
        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        arrow_down.setVisibility(View.GONE);
        arrow_up.setVisibility(View.VISIBLE);

        product_holder.setVisibility(View.GONE);
        product_holder.startAnimation(slide_down);

        try {
            recyclerView.setAdapter(null);
            productCategoryMainPojo.getData().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GuestAddress.setText(getCompleteAddressString(Current_Origin.latitude, Current_Origin.longitude));
        kilometre.setText("Vehicle Not Available");
        //SharedPrefUtil.setNearestVehicle(getActivity(),"");
        SharedPrefUtil.setSocketLiveMainPOJO(getActivity(), "");
        //scheduleTaskExecutor.shutdown();

        map.setTrafficEnabled(true);

        /*CameraPosition cameraPosition = new CameraPosition.Builder().
                target(Current_Origin).
                tilt(0).
                zoom(18).
                bearing(0).
                build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        map.addMarker(new MarkerOptions().position(Current_Origin)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer))
                .title("Origin"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSocket();
        scheduleTaskExecutor.shutdown();
        //stopSocket();
    }

    private void getVehicleRoute(String VehicleID) {
        try {

            String url = Constants.VehicleRoute + VehicleID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    SetVehicleRouteResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void SetVehicleRouteResponse(String Response) {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            JSONObject data = jsonObject.getJSONObject("data");

            JSONArray jsonArray = data.getJSONArray("routes");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                JSON_POJO js = new JSON_POJO();

                String location_name = jo_inside.getString("from_location");
                Double latitude = jo_inside.getDouble("from_lat");
                Double longitude = jo_inside.getDouble("from_lng");
                String id = jo_inside.getString("_id");
                String startTime = jo_inside.getString("startTime");
                String endTime = jo_inside.getString("endTime");

                js.setId(id);
                js.setLatitude(latitude);
                js.setLongitude(longitude);
                js.setLocation_name(location_name);
                js.setStartTime(startTime);
                js.setEndTime(endTime);

                jsonIndiaModelList.add(js);

                addRouteMarkers(id, location_name, latitude, longitude, startTime, endTime);

                if (i > 0) {
                    getRouteMapsApiDirectionsUrl(latitude, longitude);
                } else {
                    Old_Origin = new LatLng(latitude, longitude);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addRouteMarkers(String id, final String location_name, Double latitude, Double longitude, final String startTime, final String endTime) {

        try {
            /*if(markerVehicle!=null){
                markerVehicle.remove();
            }*/

            LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker_hi))
                        .title(location_name));

                data.put(TAG_ID, id);
                data.put(TAG_LOCATION_NAME, location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat sdfOutput = new SimpleDateFormat("hh:mm aa");

                Date d = sdfInput.parse(startTime);
                String startTimeFormatted = sdfOutput.format(d);

                Date d1 = sdfInput.parse(endTime);
                String endTimeFormatted = sdfOutput.format(d1);

                data.put(TAG_FROM_TIME, startTimeFormatted);
                data.put(TAG_TO_TIME, endTimeFormatted);

                extraMarkerInfo.put(marker.getId(), data);

                nearestRouteMainPOJO = SharedPrefUtil.getNearestRouteMainPOJO(getActivity());

                String[] geoArray = nearestRouteMainPOJO.getData().get(0).getDist().getLocation().getCoordinates();

                newLocation = new LatLng(Double.parseDouble(geoArray[1]), Double.parseDouble(geoArray[0]));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(Current_Origin,
                        14));

               /* markerVehicle = map.addMarker(new MarkerOptions().position(newLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        .title("TKF Vehicle"));*/
            }

            oldLocation = newLocation;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
