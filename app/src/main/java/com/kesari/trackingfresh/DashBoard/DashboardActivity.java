package com.kesari.trackingfresh.DashBoard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.OTP.SendOtpPOJO;
import com.kesari.trackingfresh.ProductMainFragment.Product_Fragment;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class DashboardActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ImageView logo,filter,map_View;
    GoogleApiClient mGoogleApiClient;
    TextView name_Login;
    String name;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Boolean exit = false;
    public static int mNotificationsCount = 0;
    //private GPSTracker gpsTracker;
    private NetworkUtilsReceiver networkUtilsReceiver;
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
    MyApplication myApplication ;
    RelativeLayout my_orders_holder,profile_holder,help_holder,route_holder;

    private Gson gson;
    VerifyMobilePOJO verifyMobilePOJO;
    SendOtpPOJO sendOtpPOJO;
    Dialog dialog;
    private ViewGroup mSnackbarContainer;
    CircleImageView profile_image;
    //ScheduledExecutorService scheduleTaskExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        try
        {

            final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            myApplication = (MyApplication) getApplicationContext();

            gson = new Gson();

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        /*toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_action_filter_list_order_sequence_sort_sorting_outline_512);*/
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            logo = (ImageView) findViewById(R.id.logo);
            filter = (ImageView) findViewById(R.id.filter);
            map_View = (ImageView) findViewById(R.id.map_View);


            map_View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
                    Product_Fragment.frameLayout.setVisibility(View.GONE);
                }
            });

           /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            my_orders_holder = (RelativeLayout) header.findViewById(R.id.my_orders_holder);
            name_Login = (TextView) header.findViewById(R.id.name_Login);
            profile_image = (CircleImageView) header.findViewById(R.id.profile_image);

            if(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage() != null)
            {
                Picasso
                        .with(DashboardActivity.this)
                        .load(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage())
                        .into(profile_image);
            }*/


           /* profile_holder = (RelativeLayout) header.findViewById(R.id.profile_holder);
            help_holder = (RelativeLayout) header.findViewById(R.id.help_holder);
            route_holder = (RelativeLayout) header.findViewById(R.id.route_holder);

            route_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, RouteActivity.class);
                    startActivity(intent);
                }
            });

            try
            {
                pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                name = SharedPrefUtil.getUser(DashboardActivity.this).getData().getFirstName();
                name_Login.setText(name);

            }catch (Exception e)
            {
                name = "Guest";
            }

            help_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            my_orders_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, OrderListActivity.class);
                    startActivity(intent);
                }
            });

            profile_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });

            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                *//*Product_Fragment product_fragment = new Product_Fragment();

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_holder, product_fragment);
                transaction.commit();*//*
                }
            });*/

//            filter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getProfileData();
//                }
//            });

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(DashboardActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            Product_Fragment product_fragment = new Product_Fragment();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_holder, product_fragment);
            transaction.commit();

            Product_Fragment.map_Holder.setVisibility(View.VISIBLE);

            updateNotificationsBadge(myApplication.getProductsArraylist().size());

            //updateNotificationsBadge(4);

            /*scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

            // This schedule a task to run every 10 minutes:
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    updateNotificationsBadge(myApplication.getProductsArraylist().size());
                }
            }, 0, 1, TimeUnit.SECONDS);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getVerifiedMobileNumber(SharedPrefUtil.getToken(DashboardActivity.this));

                }
            }, 3000);*/

            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getVerifiedMobileNumber(SharedPrefUtil.getToken(DashboardActivity.this));

                }
            }, 3000);
*/

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /*private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

            ioUtils.getPOSTStringRequestHeader(DashboardActivity.this,Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);
                    profileDataResponse(result);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponse(String Response)
    {
        try
        {
            SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

            PopupWindow popupwindow_obj = popupDisplay();
            popupwindow_obj.showAtLocation(filter, Gravity.TOP| Gravity.RIGHT, 50, 150);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }*/

    /*@Override
    protected void onRestart() {
        super.onRestart();

        Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
        Product_Fragment.frameLayout.setVisibility(View.GONE);
    }*/

    /*private void getVerifiedMobileNumber(String Token)
    {
        try
        {

            String url = Constants.VerifyMobile + SharedPrefUtil.getUser(DashboardActivity.this).getData().get_id();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + Token);

            ioUtils.getGETStringRequestHeader(DashboardActivity.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    VerifyResponse(result);

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void VerifyResponse(String Response)
    {
        try
        {

            verifyMobilePOJO = gson.fromJson(Response, VerifyMobilePOJO.class);

            if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile number not found"))
            {
                verifyMobileNumber("");
            }
            else if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile not Verified"))
            {
                verifyMobileNumber(verifyMobilePOJO.getMobileNo());
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void verifyMobileNumber(final String MobileNumber)
    {
        try {

            // Create custom dialog object
            dialog = new Dialog(DashboardActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_verify_mobile);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            final EditText mobile;
            Button confirmNumber;

            mobile = (EditText) dialog.findViewById(R.id.mobile);
            confirmNumber = (Button) dialog.findViewById(R.id.confirmNumber);

            mSnackbarContainer = (ViewGroup) dialog.findViewById(R.id.snackbar_container);

            mobile.setText(MobileNumber);

            confirmNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobile_number = mobile.getText().toString();

                    if(!mobile_number.isEmpty())
                    {
                        if (android.util.Patterns.PHONE.matcher(mobile_number).matches())
                        {
                            if (mobile_number.length() >= 10) {
                               sendMobileNumber(mobile.getText().toString(),mSnackbarContainer);
                            }
                            else
                            {
                                mobile.setError(getString(R.string.less_than_10digit));
                            }
                        }
                        else
                        {
                            mobile.setError(getString(R.string.proper_mobile));
                        }
                    }
                    else
                    {
                        mobile.setError(getString(R.string.mobileno));
                    }

                }
            });

            *//*gpsTracker = new GPSTracker(DashboardActivity.this);

            Double Lat = gpsTracker.getLatitude();
            Double Long = gpsTracker.getLongitude();*//*

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();

        }catch (Exception e)
        {
            Log.i(TAG,"dialog_Mobile");
        }

    }

    private void sendMobileNumber(final String MobileNo, ViewGroup viewGroup)
    {
        String url = Constants.SendOTP ;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobileNo", MobileNo);
            postObject.put("id",SharedPrefUtil.getUser(DashboardActivity.this).getData().get_id());

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeaderDialog(DashboardActivity.this, viewGroup, url, params ,jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                OTPResponse(result,MobileNo);
            }
        });
    }

    private void OTPResponse(String Response,String mobile)
    {
        try
        {
            sendOtpPOJO = gson.fromJson(Response, SendOtpPOJO.class);

            if(sendOtpPOJO.getMessage().equalsIgnoreCase("Otp Send"))
            {
                Intent intent = new Intent(DashboardActivity.this, OTP.class);
                intent.putExtra("mobile_num",mobile);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void SetDefaultAddress()
    {
        // Create custom dialog object
        final Dialog dialog = new Dialog(DashboardActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_default_address);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        EditText name,email,mobile,flat_no,building_name,landmark,city,state,pincode;
        Button confirmAddress;

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

        name.setText(SharedPrefUtil.getUser(DashboardActivity.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(DashboardActivity.this).getData().getLastName());
        email.setText(SharedPrefUtil.getUser(DashboardActivity.this).getData().getEmailId());

        //gpsTracker = new GPSTracker(DashboardActivity.this);

        Location Current_Origin = SharedPrefUtil.getLocation(DashboardActivity.this);

        Double Lat = Current_Origin.getLatitude();
        Double Long = Current_Origin.getLongitude();

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

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1500;
        window.setAttributes(lp);

        dialog.show();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tocart, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        setBadgeCount(this, icon, mNotificationsCount);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hot:
                Intent intent = new Intent(DashboardActivity.this, AddToCart.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
    }

    /*@Override
    protected void onStart() {

        try
        {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();
            super.onStart();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }



    public PopupWindow popupDisplay()
    {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_header_user, null);

        TextView nameTxt = (TextView) view.findViewById(R.id.name);
        nameTxt.setText("Hello " + name);

        TextView my_account = (TextView) view.findViewById(R.id.my_account);
        TextView my_orders = (TextView) view.findViewById(R.id.my_orders);

        my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        CircleImageView imgUserimage = (CircleImageView) view.findViewById(R.id.imgUserimage);

        if(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage() != null)
        {
            Picasso
                    .with(DashboardActivity.this)
                    .load(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage())
                    .into(imgUserimage);
        }

        Button logout = (Button) view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent i=new Intent(DashboardActivity.this,LoginActivity.class);
                                startActivity(i);

                                editor = pref.edit();
                                editor.clear();
                                editor.commit();
                            }
                        });

                SharedPrefUtil.setClear(DashboardActivity.this);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        popupWindow.setFocusable(true);
        popupWindow.setWidth(width-140);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }
*/
   /* @Override
    public void onBackPressed() {
        super.onBackPressed();

        Product_Fragment product_fragment = new Product_Fragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_holder, product_fragment);
        transaction.commit();
    }*/

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
            Product_Fragment.frameLayout.setVisibility(View.GONE);
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            //scheduleTaskExecutor.shutdown();

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
