package com.kesari.trackingfresh.ProductPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.kesari.trackingfresh.DetailPage.DetailsActivity;
import com.kesari.trackingfresh.Map.GPSTracker;
import com.kesari.trackingfresh.Map.HttpConnection;
import com.kesari.trackingfresh.Map.JSON_POJO;
import com.kesari.trackingfresh.Map.PathJSONParser;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.IOUtils;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 11/04/17.
 */

public class Product_Fragment extends Fragment implements OnMapReadyCallback
{

    private LatLng Current_Origin;

    //GoogleMap googleMap;
    final String TAG = "GuestRoute";
    private GPSTracker gps;
    private double latitude;
    private double longitude;

    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    List<Product_POJO> product_pojos = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME= "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    GridView recyclerView;
    private LinearLayoutManager llm;

    //private RecyclerView.Adapter adapterItineraryDetails;
    private MyDataAdapter myDataAdapter;

    private Context mContext;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    /*private MarkerOptions currentPositionMarker = null;
    private Marker currentLocationMarker;*/
    ImageView fruits,vegetables,groceries;
    View f1;
    FrameLayout fragment_data;
    Marker marker;

    LatLng oldLocation, newLocation;
    LatLng latlang;

    boolean isMarkerRotating = false;

    SearchView searchView;
    PlacesAutocompleteTextView placesAutocompleteTextView;
    TextView kilometre,GuestAddress;
    RelativeLayout map_Holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product, container, false);

        fruits = (ImageView) V.findViewById(R.id.fruits);
        vegetables = (ImageView) V.findViewById(R.id.vegetables);
        groceries = (ImageView) V.findViewById(R.id.groceries);
        f1 = (View) V.findViewById(R.id.map_container);
        fragment_data = (FrameLayout) V.findViewById(R.id.fragment_data);

        searchView = (SearchView) V.findViewById(R.id.searchLocation);
        placesAutocompleteTextView = (PlacesAutocompleteTextView) V.findViewById(R.id.places_autocomplete);

        kilometre = (TextView) V.findViewById(R.id.kilometre);
        GuestAddress = (TextView) V.findViewById(R.id.GuestAddress);
        map_Holder = (RelativeLayout) V.findViewById(R.id.map_Holder);

        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place

                        Geocoder coder = new Geocoder(getActivity());
                        try {
                            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(place.description, 50);
                            for(Address add : adresses){

                                    double longitude = add.getLongitude();
                                    double latitude = add.getLatitude();

                                Current_Origin = new LatLng(latitude,longitude);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        gps = new GPSTracker(getActivity());

        Current_Origin = new LatLng(gps.getLatitude(),gps.getLongitude());

        Log.i("latitude", String.valueOf(gps.getLatitude()));
        Log.i("longitude", String.valueOf(gps.getLongitude()));

        oldLocation = Current_Origin;

        fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product_categoryFragment product_categoryFragment = new Product_categoryFragment();

                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_data, product_categoryFragment);
                transaction.commit();

                map_Holder.setVisibility(View.GONE);

            }
        });

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //map.clear();

                //getData();
            }
        });

        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //getData();

        map = googleMap;
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(gps.getLatitude());
        location.setLongitude(gps.getLongitude());

        updateCurrentLocationMarker(location);

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(Current_Origin).
                tilt(60).
                zoom(18).
                bearing(0).
                build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.addMarker(new MarkerOptions().position(Current_Origin)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
                .title("Origin"));

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(Current_Origin, 18.0f));

        ScheduledExecutorService scheduleTaskExecutor= Executors.newScheduledThreadPool(5);

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
                }
                else
                {
                    getDriverLocationTask();
                }
            }
        }, 0, 3, TimeUnit.SECONDS);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                try
                {
                    // Get extra data with marker ID
                    HashMap<String, String> marker_data = extraMarkerInfo.get(marker.getId());

                    String LatLng = String.valueOf(marker_data.get(TAG_LOCATION_NAME));

                    // Create custom dialog object
                    final Dialog dialog = new Dialog(getActivity());
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.dialog);
                    // Set dialog title
                    dialog.setTitle("Custom Dialog");

                    ImageView cancel_action = (ImageView) dialog.findViewById(R.id.cancel_action);

                    cancel_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = dialog.getWindow();
                    lp.copyFrom(window.getAttributes());

                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(lp);

                    recyclerView = (GridView) dialog.findViewById(R.id.list);

                    //recyclerView.setHasFixedSize(true);
                    llm = new LinearLayoutManager(getActivity());
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    //recyclerView.setLayoutManager(llm);

                    dialog.show();

                    getProductData();

                    Toast.makeText(getActivity(), LatLng, Toast.LENGTH_SHORT).show();

                }
                catch (NullPointerException npe)
                {

                }

                return false;
            }
        });

        /*final LatLng startPosition = marker.getPosition();
        final LatLng finalPosition = new LatLng(gps.getLatitude(), gps.getLongitude());
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 30000;
        final boolean hideMarker = false;
        //bearingBetweenLocations(SomePos,finalPosition);

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                marker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });*/
    }

    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

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

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    public void updateCurrentLocationMarker(Location currentLatLng){

        if(map != null){

            /*LatLng latLng = new LatLng(currentLatLng.getLatitude(),currentLatLng.getLongitude());
            if(currentPositionMarker == null){
                currentPositionMarker = new MarkerOptions();

                currentPositionMarker.position(latLng)
                        .title("My Location").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.van));
                currentLocationMarker = map.addMarker(currentPositionMarker);
            }

            if(currentLocationMarker != null)
                currentLocationMarker.setPosition(latLng);

            ///currentPositionMarker.position(latLng);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/

            //getData();
        }
    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        String waypoints = "waypoints=optimize:true|"
                + Current_Origin.latitude + "," + Current_Origin.longitude
                + "|" + "|" + destLatitude + ","
                + destLongitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?"+"origin="+Current_Origin.latitude + "," + Current_Origin.longitude+"&destination="+destLatitude + ","
                + destLongitude +"&" + params;

        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

    }

    private void addMarkers(String id,String location_name,Double latitude,Double longitude) {

        LatLng dest = new LatLng(latitude, longitude);

        HashMap<String, String> data = new HashMap<String, String>();

        if (map != null) {
            marker = map.addMarker(new MarkerOptions().position(dest)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_delivery_van))
                    .title(location_name));

            data.put(TAG_ID,id);
            data.put(TAG_LOCATION_NAME,location_name);
            data.put(TAG_LATITUDE, String.valueOf(latitude));
            data.put(TAG_LONGITUDE, String.valueOf(longitude));

            extraMarkerInfo.put(marker.getId(),data);

            map.addMarker(new MarkerOptions().position(Current_Origin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))
                    .title("Origin"));

            //float bearing = (float) bearing(convertLatLngToLocation(oldLocation),convertLatLngToLocation(newLocation))
            //rotateMarker(marker, bearing);
        }

        oldLocation = newLocation;
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    private double bearing(Location startPoint, Location endPoint) {
        double longitude1 = startPoint.getLongitude();
        double latitude1 = Math.toRadians(startPoint.getLatitude());

        double longitude2 = endPoint.getLongitude();
        double latitude2 = Math.toRadians(endPoint.getLatitude());

        double longDiff = Math.toRadians(longitude2 - longitude1);

        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return Math.toDegrees(Math.atan2(y, x));
    }

    public void getData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                JSON_POJO js = new JSON_POJO();

                String location_name = jo_inside.getString("location_name");
                Double latitude = jo_inside.getDouble("latitude");
                Double longitude = jo_inside.getDouble("longitude");
                String id = jo_inside.getString("id");

                js.setId(id);
                js.setLatitude(latitude);
                js.setLongitude(longitude);
                js.setLocation_name(location_name);

                jsonIndiaModelList.add(js);

                addMarkers(id,location_name,latitude,longitude);
                //getMapsApiDirectionsUrl(latitude,longitude);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getProductData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadProductJSONFromAsset());


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                Product_POJO js = new Product_POJO();

                String product_name = jo_inside.getString("product_name");
                String images = jo_inside.getString("images");
                String id = jo_inside.getString("id");
                String kilo = jo_inside.getString("kilo");
                String Rs = jo_inside.getString("Rs");

                js.setId(id);
                js.setImages(images);
                js.setProduct_name(product_name);
                js.setKilo(kilo);
                js.setRs(Rs);

                product_pojos.add(js);

            }

            Collections.shuffle(product_pojos);

            myDataAdapter = new MyDataAdapter(product_pojos,getActivity());
            recyclerView.setAdapter(myDataAdapter);
            myDataAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyDataAdapter<T> extends BaseAdapter {
        List<Product_POJO> Product_POJOs;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<Product_POJO> Product_POJOs, Activity activity) {
            this.Product_POJOs =  Product_POJOs;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return Product_POJOs.size();
        }

        @Override
        public Object getItem(int position) {
            return Product_POJOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.product_layout, null);

                viewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

                viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.price);

                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (FancyButton) convertView.findViewById(R.id.plus);
                viewHolder.minus = (FancyButton) convertView.findViewById(R.id.minus);

                viewHolder.addtoCart = (Button) convertView.findViewById(R.id.addtoCart);
                viewHolder.holder_count = (LinearLayout) convertView.findViewById(R.id.holder_count);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Product_POJO product_pojo = product_pojos.get(position);

            viewHolder.product_name.setText(product_pojo.getProduct_name());

            viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity,product_pojo.getImages()));
            viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));

            viewHolder.weight.setText(product_pojo.getKilo());
            viewHolder.price.setText(product_pojo.getRs());

            viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.holder_count.setVisibility(View.VISIBLE);
                    viewHolder.addtoCart.setVisibility(View.GONE);
                }
            });

            viewHolder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        int t = Integer.parseInt(viewHolder.count.getText().toString());
                        viewHolder.count.setText(String.valueOf(t+1));
                    }
                    catch (Exception e)
                    {

                    }
                }
            });

            viewHolder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int t = Integer.parseInt(viewHolder.count.getText().toString());
                        if(t > 0)
                        {
                            viewHolder.count.setText(String.valueOf(t-1));
                        }

                        if(t < 0 || t == 0)
                        {
                            viewHolder.holder_count.setVisibility(View.GONE);
                            viewHolder.addtoCart.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e)
                    {

                    }
                }
            });

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(activity, DetailsActivity.class);
                    startActivity(in);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView product_name,weight,price,count;
            SimpleDraweeView imageView;
            FancyButton plus,minus;
            Button addtoCart;
            LinearLayout holder_count;
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("mock_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadProductJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("products_mock.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
            Log.i("ReadTaskResult",result);
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
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public void getDriverLocationTask()
    {
        //String url = "http://192.168.1.220:8000/api/vehicle_positions/by_driver_id/dr001";
        //String url = "http://115.112.155.181:8000/api/vehicle_positions/by_driver_id/dr001";

        String url = Constants.LocationAPI;

        Log.i("url",url);

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());
                DriverLocationResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    public void DriverLocationResponse(String resp)
    {
        map.clear();
            try
            {
                JSONObject jsonObject = new JSONObject(resp);

                JSONObject dataObject = jsonObject.getJSONObject("data");

                String created_at = dataObject.getString("created_at");

                JSONArray geoArray = dataObject.getJSONArray("geo");

                Double cust_longitude = geoArray.getDouble(0);
                Double cust_latitude = geoArray.getDouble(1);

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

                addMarkers("1","TKF Vehicle",cust_latitude,cust_longitude);
                getMapsApiDirectionsUrl(cust_latitude,cust_longitude);

                latlang = new LatLng(cust_latitude,cust_longitude );

                Location loc1 = new Location("");
                loc1.setLatitude(Current_Origin.latitude);
                loc1.setLongitude(Current_Origin.longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(latlang.latitude);
                loc2.setLongitude(latlang.longitude);

                float distanceInMeters = loc1.distanceTo(loc2)/1000;

                Log.i("Distance",String.valueOf(distanceInMeters));

                String VehicleAddress = IOUtils.getCompleteAddressString(getActivity(),latlang.latitude,latlang.longitude);

                String CustomerAddress = IOUtils.getCompleteAddressString(getActivity(),Current_Origin.latitude,Current_Origin.longitude);

                kilometre.setText("Vehicle is " + String.valueOf(IOUtils.roundToOneDigit(distanceInMeters)) + " kms away at " + VehicleAddress);

                GuestAddress.setText("Your Address: " + CustomerAddress);

            }catch (JSONException e)
            {
                Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            }
    }

}
