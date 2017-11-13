package com.trackingfresh.customer.DashBoard;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.trackingfresh.customer.Cart.AddToCart;
import com.trackingfresh.customer.ChangePassword.ChangePasswordActivity;
import com.trackingfresh.customer.HelpAndFAQ.HelpActivity;
import com.trackingfresh.customer.Legal.LegalActivity;
import com.trackingfresh.customer.Login.LoginActivity;
import com.trackingfresh.customer.Map.LocationServiceNew;
import com.trackingfresh.customer.Map.RestartServiceReceiver;
import com.trackingfresh.customer.MyOffers.MyOffersActivity;
import com.trackingfresh.customer.MyProfile.ProfileActivity;
import com.trackingfresh.customer.NotificationList.NotificationListActivity;
import com.trackingfresh.customer.OTP.OTP;
import com.trackingfresh.customer.OTP.SendOtpPOJO;
import com.trackingfresh.customer.ProductMainFragment.Product_Fragment;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.ReferEarn.ReferralCodeActivity;
import com.trackingfresh.customer.Settings.SettingsActivity;
import com.trackingfresh.customer.TKCash.TKWalletActivity;
import com.trackingfresh.customer.Utilities.Constants;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;
import com.trackingfresh.customer.VehicleRoute.RouteActivity;
import com.trackingfresh.customer.YourOrders.OrderListActivity;
import com.trackingfresh.customer.network.MyApplication;
import com.trackingfresh.customer.network.NetworkUtils;
import com.trackingfresh.customer.network.NetworkUtilsReceiver;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.trackingfresh.customer.Utilities.IOUtils.setBadgeCount;

public class DashboardActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private String TAG = this.getClass().getSimpleName();
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ImageView logo, filter, map_View;
    static GoogleApiClient mGoogleApiClient;
    TextView name_Login;
    String name;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
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
    public static MyApplication myApplication;
    RelativeLayout my_orders_holder, menu_holder,my_cart_holder,notification_holder, help_holder, route_holder, refer_earn, legalHolder,setting_layout,my_offers_holder,tkfRelativeLayout,restPassRelativeLayout,logoutRelativeLayout;
    TextView profile_holder;
    private Gson gson;
    VerifyMobilePOJO verifyMobilePOJO;
    SendOtpPOJO sendOtpPOJO;
    Dialog dialog;
    private ViewGroup mSnackbarContainer;
    CircleImageView profile_image;
    //ScheduledExecutorService scheduleTaskExecutor;
    TextView walletAmount,menuTextView,mapTextView;

    PopupWindow popupwindow_obj;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        try {

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this,R.color.porcelain));

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            getSupportActionBar().setHomeAsUpIndicator(drawable);
            myApplication = (MyApplication) getApplicationContext();

            gson = new Gson();

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            // Retrieve a PendingIntent that will perform a broadcast
            Intent alarmIntent = new Intent(this, RestartServiceReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

            startAlarm();

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
            mapTextView = (TextView) findViewById(R.id.mapTextView);
            menuTextView = (TextView) findViewById(R.id.menuTextView);


            menuTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuTextView.setBackgroundColor(getResources().getColor(R.color.MoneyGreen));
                    mapTextView.setBackgroundColor(getResources().getColor(R.color.whitegray));
                    menuTextView.setTextColor(getResources().getColor(R.color.white));
                    mapTextView.setTextColor(getResources().getColor(R.color.gray));
                    Product_Fragment.fragment_holder.setVisibility(View.GONE);
                    Product_Fragment.layout_holder.setVisibility(View.VISIBLE);
                    Product_Fragment.frameLayout.setVisibility(View.GONE);
                    Product_Fragment.product_holder.setVisibility(View.VISIBLE);
                }
            });


            mapTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mapTextView.setBackgroundColor(getResources().getColor(R.color.MoneyGreen));
                    menuTextView.setBackgroundColor(getResources().getColor(R.color.whitegray));
                    menuTextView.setTextColor(getResources().getColor(R.color.gray));
                    mapTextView.setTextColor(getResources().getColor(R.color.white));
                    Product_Fragment.product_holder.setVisibility(View.GONE);

                    Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
                    Product_Fragment.fragment_holder.setVisibility(View.VISIBLE);
                    Product_Fragment.layout_holder.setVisibility(View.GONE);
                    Product_Fragment.frameLayout.setVisibility(View.GONE);}
            });

            getProfileDataOnCreate();

            map_View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mapTextView.setBackgroundColor(getResources().getColor(R.color.MoneyGreen));
                    menuTextView.setBackgroundColor(getResources().getColor(R.color.whitegray));
                    menuTextView.setTextColor(getResources().getColor(R.color.gray));
                    mapTextView.setTextColor(getResources().getColor(R.color.white));
                    Product_Fragment.product_holder.setVisibility(View.GONE);

                    Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
                    Product_Fragment.fragment_holder.setVisibility(View.VISIBLE);
                    Product_Fragment.layout_holder.setVisibility(View.GONE);
                    Product_Fragment.frameLayout.setVisibility(View.GONE);

//                    Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
//                    Product_Fragment.frameLayout.setVisibility(View.GONE);
                }
            });

            final DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            mDrawerLayout.closeDrawers();
            final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            my_orders_holder = (RelativeLayout) header.findViewById(R.id.my_orders_holder);
            name_Login = (TextView) header.findViewById(R.id.name_Login);

            profile_holder = (TextView) header.findViewById(R.id.profile_holder);
            help_holder = (RelativeLayout) header.findViewById(R.id.help_holder);
            route_holder = (RelativeLayout) header.findViewById(R.id.route_holder);
            refer_earn = (RelativeLayout) header.findViewById(R.id.refer_earn);
            profile_image = (CircleImageView) header.findViewById(R.id.profile_image);
            walletAmount = (TextView) header.findViewById(R.id.walletAmount);
            legalHolder = (RelativeLayout) header.findViewById(R.id.legalHolder);
            setting_layout = (RelativeLayout) header.findViewById(R.id.setting_layout);
            my_offers_holder = (RelativeLayout) header.findViewById(R.id.my_offers_holder);
            notification_holder = (RelativeLayout) header.findViewById(R.id.notification_holder);
            my_cart_holder = (RelativeLayout) header.findViewById(R.id.my_cart_holder);
            menu_holder = (RelativeLayout) header.findViewById(R.id.menu_holder);
            tkfRelativeLayout = (RelativeLayout) header.findViewById(R.id.tkfRelativeLayout);
            restPassRelativeLayout = (RelativeLayout) header.findViewById(R.id.restPassRelativeLayout);
            logoutRelativeLayout = (RelativeLayout) header.findViewById(R.id.logoutRelativeLayout);

            my_offers_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, MyOffersActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            setting_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            refer_earn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, ReferralCodeActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            route_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, RouteActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            pref = getApplicationContext().getSharedPreferences("MyPref", 0);

            help_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, HelpActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            legalHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, LegalActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            my_orders_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, OrderListActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            profile_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });
            tkfRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, TKWalletActivity.class);
                    startActivity(intent);

                    mDrawerLayout.closeDrawers();

                }
            });
            restPassRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });
            logoutRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogOutFunc(DashboardActivity.this);

                    mDrawerLayout.closeDrawers();

                }
            });
            menu_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    menuTextView.setBackgroundColor(getResources().getColor(R.color.MoneyGreen));
                    mapTextView.setBackgroundColor(getResources().getColor(R.color.whitegray));
                    menuTextView.setTextColor(getResources().getColor(R.color.white));
                    mapTextView.setTextColor(getResources().getColor(R.color.gray));
                    Product_Fragment.fragment_holder.setVisibility(View.GONE);
                    Product_Fragment.layout_holder.setVisibility(View.VISIBLE);
                    Product_Fragment.frameLayout.setVisibility(View.GONE);
                    Product_Fragment.product_holder.setVisibility(View.VISIBLE);
                    mDrawerLayout.closeDrawers();

                }
            });
            my_cart_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, AddToCart.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();


                }
            });
            notification_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashboardActivity.this, NotificationListActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawers();

                }
            });

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProfileData();
                }
            });

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                IOUtils.buildAlertMessageNoGps(DashboardActivity.this);
            } else {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getVerifiedMobileNumber(SharedPrefUtil.getToken(DashboardActivity.this));

                }
            }, 3000);

            Product_Fragment product_fragment = new Product_Fragment();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_holder, product_fragment);
            transaction.commit();

            Log.i("Cust_Auth", SharedPrefUtil.getToken(DashboardActivity.this));

            try {
                if (SharedPrefUtil.getFirebaseToken(DashboardActivity.this) != null) {
                    Log.i("FirebaseTOKEN", SharedPrefUtil.getFirebaseToken(DashboardActivity.this));
                    sendToken(SharedPrefUtil.getFirebaseToken(DashboardActivity.this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(myApplication.getProductsArraylist() != null)
            {
                updateNotificationsBadge(myApplication.getProductsArraylist().size());
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    private void sendToken(String TOKEN) {
        try {

            String url = Constants.FirebaseToken;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("FBT", TOKEN);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(DashboardActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

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

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

            ioUtils.getPOSTStringRequestHeader(DashboardActivity.this, Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result", result);
                    profileDataResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponse(String Response) {
        try {
            SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

            if (SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount() != null) {
                if (!SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount().isEmpty()) {
                    walletAmount.setText(SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount());
                } else {
                    walletAmount.setText("0");
                }
            }

            if (SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage() != null) {
                Picasso
                        .with(DashboardActivity.this)
                        .load(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage())
                        .into(profile_image);
            }

            try {
                name = SharedPrefUtil.getUser(DashboardActivity.this).getData().getFirstName();
                name_Login.setText(name);

            } catch (Exception e) {
                name = "Guest";
            }

            popupwindow_obj = popupDisplay();
            popupwindow_obj.showAtLocation(filter, Gravity.TOP | Gravity.RIGHT, 50, 150);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getProfileDataOnCreate() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

            ioUtils.getPOSTStringRequestHeader(DashboardActivity.this, Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result", result);
                    profileDataResponseOnCreate(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponseOnCreate(String Response) {
        try {
            SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

            if (SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount() != null) {
                if (!SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount().isEmpty()) {
                    walletAmount.setText(SharedPrefUtil.getUser(DashboardActivity.this).getData().getWalletAmount());
                } else {
                    walletAmount.setText("0");
                }
            }

            if (SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage() != null) {
                Picasso
                        .with(DashboardActivity.this)
                        .load(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage())
                        .into(profile_image);
            }

            try {
                name = SharedPrefUtil.getUser(DashboardActivity.this).getData().getFirstName();
                name_Login.setText(name);

            } catch (Exception e) {
                name = "Guest";
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
try {
    Product_Fragment.map_Holder.setVisibility(View.VISIBLE);
    Product_Fragment.frameLayout.setVisibility(View.GONE);
}catch (Exception e){}
    }

    private void getVerifiedMobileNumber(String Token) {
        try {

            String url = Constants.VerifyMobile + SharedPrefUtil.getUser(DashboardActivity.this).getData().get_id();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + Token);

            ioUtils.getGETStringRequestHeader(DashboardActivity.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    VerifyResponse(result);

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

    private void VerifyResponse(String Response) {
        try {

            verifyMobilePOJO = gson.fromJson(Response, VerifyMobilePOJO.class);

            if (verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile number not found")) {
                verifyMobileNumber("");
            } else if (verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile not Verified")) {
                verifyMobileNumber(verifyMobilePOJO.getMobileNo());
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void verifyMobileNumber(final String MobileNumber) {
        try {

            // Create custom dialog object
            dialog = new Dialog(DashboardActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_verify_mobile);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            final EditText mobile;
            FancyButton confirmNumber;

            mobile = (EditText) dialog.findViewById(R.id.mobile);
            confirmNumber = (FancyButton) dialog.findViewById(R.id.confirmNumber);

            mSnackbarContainer = (ViewGroup) dialog.findViewById(R.id.snackbar_container);

            mobile.setText(MobileNumber);

            confirmNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mobile_number = mobile.getText().toString();

                    if (!mobile_number.isEmpty()) {
                        if (android.util.Patterns.PHONE.matcher(mobile_number).matches()) {
                            if (mobile_number.length() >= 10) {
                                sendMobileNumber(mobile.getText().toString(), mSnackbarContainer);
                            } else {
                                mobile.setError(getString(R.string.less_than_10digit));
                            }
                        } else {
                            mobile.setError(getString(R.string.proper_mobile));
                        }
                    } else {
                        mobile.setError(getString(R.string.mobileno));
                    }

                }
            });

            /*gpsTracker = new GPSTracker(DashboardActivity.this);

            Double Lat = gpsTracker.getLatitude();
            Double Long = gpsTracker.getLongitude();*/

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();

        } catch (Exception e) {
            Log.i(TAG, "dialog_Mobile");
        }

    }

    private void sendMobileNumber(final String MobileNo, ViewGroup viewGroup) {
        String url = Constants.SendOTP;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobileNo", MobileNo);
            postObject.put("id", SharedPrefUtil.getUser(DashboardActivity.this).getData().get_id());

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DashboardActivity.this));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeaderDialog(DashboardActivity.this, viewGroup, url, params, jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                OTPResponse(result, MobileNo);
            }
        }, new IOUtils.VolleyFailureCallback() {
            @Override
            public void onFailure(String result) {

            }
        });
    }

    private void OTPResponse(String Response, String mobile) {
        try {
            sendOtpPOJO = gson.fromJson(Response, SendOtpPOJO.class);

            if (sendOtpPOJO.getMessage().equalsIgnoreCase("Otp Send")) {
                Intent intent = new Intent(DashboardActivity.this, OTP.class);
                intent.putExtra("mobile_num", mobile);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

   /* private void SetDefaultAddress()
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
        LayerDrawable iconLayer = (LayerDrawable) item.getIcon();
//        BitmapDrawable iconBitmap = (BitmapDrawable) item.getIcon();
//        LayerDrawable iconLayer = new LayerDrawable(new Drawable [] { iconBitmap });
        setBadgeCount(this, iconLayer, mNotificationsCount);

        return super.onCreateOptionsMenu(menu);

//        MenuItem itemCart = menu.findItem(R.id.menu_hot);
//        BitmapDrawable iconBitmap = (BitmapDrawable) itemCart.getIcon();
//        LayerDrawable iconLayer = new LayerDrawable(new Drawable [] { iconBitmap });
//        setBadgeCount(this, iconLayer, mNotificationsCount);
//        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onStart() {

        try {

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


    public PopupWindow popupDisplay() {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_header_user, null);

        TextView nameTxt = (TextView) view.findViewById(R.id.name);
        nameTxt.setText("Hello " + name);


        TextView my_account = (TextView) view.findViewById(R.id.my_account);
        TextView my_orders = (TextView) view.findViewById(R.id.my_orders);
        TextView tkcash = (TextView) view.findViewById(R.id.tkcash);
        TextView notificationList = (TextView) view.findViewById(R.id.notificationList);

        TextView change_password = (TextView) view.findViewById(R.id.change_password);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

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

        tkcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, TKWalletActivity.class);
                startActivity(intent);
            }
        });

        notificationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NotificationListActivity.class);
                startActivity(intent);
            }
        });

        CircleImageView imgUserimage = (CircleImageView) view.findViewById(R.id.imgUserimage);

        if (SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage() != null) {
            Picasso
                    .with(DashboardActivity.this)
                    .load(SharedPrefUtil.getUser(DashboardActivity.this).getData().getProfileImage())
                    .into(imgUserimage);
        }

        FancyButton logout = (FancyButton) view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutFunc(DashboardActivity.this);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        popupWindow.setFocusable(true);
        popupWindow.setWidth(width - 140);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    public static void LogOutFunc(final Context context)
    {
        LoginManager.getInstance().logOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show();
                        /*new SweetAlertDialog(context)
                                .setTitleText("Logged Out")
                                .show();*/

                        Intent i = new Intent(context, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                        editor = pref.edit();
                        editor.clear();
                        editor.commit();
                    }
                });

        SharedPrefUtil.setClear(context);

        myApplication.removeProductsItems();
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
        try {
            if (exit) {
                finishAffinity(); // finish activity
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
        }catch (Exception e){
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            //scheduleTaskExecutor.shutdown();

            if ( popupwindow_obj !=null && popupwindow_obj.isShowing() ){
                popupwindow_obj.dismiss();
            }

            /*if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }*/

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void NetworkOpen() {
            //checkFirstRun();
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

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
