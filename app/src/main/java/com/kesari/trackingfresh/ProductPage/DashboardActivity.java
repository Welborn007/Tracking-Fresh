package com.kesari.trackingfresh.ProductPage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.Map.GPSTracker;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Register.OTP;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.MyApplication;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class DashboardActivity extends AppCompatActivity {

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ImageView logo,filter;
    GoogleApiClient mGoogleApiClient;
    TextView name_Login;
    String name;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Boolean exit = false;
    public static int mNotificationsCount = 0;
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

    MyApplication myApplication ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        myApplication = (MyApplication) getApplicationContext();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        /*toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_action_filter_list_order_sequence_sort_sorting_outline_512);*/
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        logo = (ImageView) findViewById(R.id.logo);
        filter = (ImageView) findViewById(R.id.filter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        name_Login = (TextView) header.findViewById(R.id.name_Login);

        try
        {
            pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            name = SharedPrefUtil.getUser(DashboardActivity.this).getUser().getFirstName();
            name_Login.setText(name);

        }catch (Exception e)
        {
            name = "Guest";
        }

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Product_Fragment product_fragment = new Product_Fragment();

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_holder, product_fragment);
                transaction.commit();*/
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupWindow popupwindow_obj = popupDisplay();
//                popupwindow_obj.showAsDropDown(profile);
                popupwindow_obj.showAtLocation(filter, Gravity.TOP| Gravity.RIGHT, 50, 150);
            }
        });

        final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
        {
            buildAlertMessageNoGps();
        }
        else
        {

        }

        Product_Fragment product_fragment = new Product_Fragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_holder, product_fragment);
        transaction.commit();

        //updateNotificationsBadge(4);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a task to run every 10 minutes:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                DashboardActivity.updateNotificationsBadge(myApplication.getProductsArraylist().size());
            }
        }, 0, 1, TimeUnit.SECONDS);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               //SetDefaultAddress();
                verifyMobileNumber();
            }
        }, 3000);
    }

    private void verifyMobileNumber()
    {
        // Create custom dialog object
        final Dialog dialog = new Dialog(DashboardActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_verify_mobile);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        final EditText mobile;
        Button confirmNumber;

        mobile = (EditText) dialog.findViewById(R.id.mobile);
        confirmNumber = (Button) dialog.findViewById(R.id.confirmNumber);

        confirmNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile_number = mobile.getText().toString();

                if(!mobile_number.isEmpty())
                {
                    if (android.util.Patterns.PHONE.matcher(mobile_number).matches())
                    {
                        if (mobile_number.length() >= 10) {
                            Intent intent = new Intent(DashboardActivity.this, OTP.class);
                            intent.putExtra("mobile_num",mobile_number);
                            startActivity(intent);
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

        gpsTracker = new GPSTracker(DashboardActivity.this);

        Double Lat = gpsTracker.getLatitude();
        Double Long = gpsTracker.getLongitude();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.show();
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

        name.setText(SharedPrefUtil.getUser(DashboardActivity.this).getUser().getFirstName() + " " + SharedPrefUtil.getUser(DashboardActivity.this).getUser().getLastName());
        email.setText(SharedPrefUtil.getUser(DashboardActivity.this).getUser().getEmailId());

        gpsTracker = new GPSTracker(DashboardActivity.this);

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

    }

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public PopupWindow popupDisplay()
    {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_header_user, null);

        TextView nameTxt = (TextView) view.findViewById(R.id.name);
        nameTxt.setText("Hello " + name);

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
}
