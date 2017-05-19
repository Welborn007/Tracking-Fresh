package com.kesari.trackingfresh.ProductPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


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
                Product_Fragment product_fragment = new Product_Fragment();

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_holder, product_fragment);
                transaction.commit();
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
