package com.kesari.trackingfresh.Register;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.Login.LoginMain;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    EditText first_name, last_name, mobile, email, location, referral_code, password;
    Button btnRegister;
    private String TAG = this.getClass().getSimpleName();

    String SocialID = "", FirstName, LastName, Name, Email, Type = "simple",Mobile,Referral_code,Password;
    GoogleApiClient mGoogleApiClient;
    private NetworkUtilsReceiver networkUtilsReceiver;

    private Gson gson;
    LoginMain loginMain;
    Boolean duplicateMobile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.porcelain));

            setTitle("Register");
            toolbar.setTitleTextColor(ContextCompat.getColor(RegisterActivity.this,R.color.white));

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(RegisterActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            first_name = (EditText) findViewById(R.id.first_name);
            last_name = (EditText) findViewById(R.id.last_name);
            mobile = (EditText) findViewById(R.id.mobile);
            email = (EditText) findViewById(R.id.email);
            location = (EditText) findViewById(R.id.location);
            referral_code = (EditText) findViewById(R.id.referral_code);
            password = (EditText) findViewById(R.id.password);

            try {
                SocialID = getIntent().getStringExtra("SocialID");
                Name = getIntent().getStringExtra("Name");
                FirstName = getIntent().getStringExtra("firstname");
                LastName = getIntent().getStringExtra("lastname");
                Email = getIntent().getStringExtra("Email");
                Type = getIntent().getStringExtra("Type");

                first_name.setText(FirstName);
                last_name.setText(LastName);
                email.setText(Email);

            } catch (NullPointerException npe) {
                Log.i("Null", "Null");
                SocialID = "";
                Type = "simple";
            }

            btnRegister = (Button) findViewById(R.id.btnRegister);

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirstName = first_name.getText().toString();
                    LastName = last_name.getText().toString();
                    Mobile = mobile.getText().toString();
                    Email = email.getText().toString();
                    //String Location = location.getText().toString();
                    Referral_code = referral_code.getText().toString();
                    Password = password.getText().toString();

                    if (!FirstName.isEmpty() && !LastName.isEmpty() && !Mobile.isEmpty() && !Email.isEmpty() && !Password.isEmpty()) {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches() && android.util.Patterns.PHONE.matcher(Mobile).matches()) {
                            //int mob = Integer.parseInt(Mobile);

                            if(FirstName.length() >= 2 && LastName.length() >= 2)
                            {
                                if(FirstName.matches("^[ A-Za-z]+$") && LastName.matches("^[ A-Za-z]+$"))
                                {
                                    if(Mobile.matches("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$"))
                                    {
                                        if (Mobile.length() >= 10) {
                                            if (!NetworkUtils.isNetworkConnectionOn(RegisterActivity.this)) {

                                                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.NORMAL_TYPE)
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
                                                btnRegister.setClickable(false);
                                                verifyDuplicateMobileAndRegister(mobile.getText().toString().trim());
                                            }
                                        } else {
                                            //Toast.makeText(RegisterActivity.this, getString(R.string.less_than_10digit) , Toast.LENGTH_SHORT).show();
                                            mobile.setError(getString(R.string.less_than_10digit));
                                            mobile.requestFocus();
                                        }
                                    }
                                    else
                                    {
                                        mobile.setError(getString(R.string.proper_mobile));
                                        mobile.requestFocus();
                                    }
                                }
                                else if(!FirstName.matches("^[ A-Za-z]+$"))
                                {
                                    first_name.setError(getString(R.string.valid_name));
                                    first_name.requestFocus();
                                }
                                else if(!LastName.matches("^[ A-Za-z]+$"))
                                {
                                    last_name.setError(getString(R.string.valid_name));
                                    last_name.requestFocus();
                                }

                            }
                            else if(FirstName.length() < 2)
                            {
                                first_name.setError(getString(R.string.first_name_2_char));
                                first_name.requestFocus();
                            }
                            else if(LastName.length() < 2)
                            {
                                last_name.setError(getString(R.string.last_name_2_char));
                                last_name.requestFocus();
                            }

                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                            //Toast.makeText(RegisterActivity.this, getString(R.string.proper_email), Toast.LENGTH_SHORT).show();
                            email.setError(getString(R.string.proper_email));
                            email.requestFocus();
                        } else if (!android.util.Patterns.PHONE.matcher(Mobile).matches()) {
                            //Toast.makeText(RegisterActivity.this, getString(R.string.proper_mobile), Toast.LENGTH_SHORT).show();
                            mobile.setError(getString(R.string.proper_mobile));
                            mobile.requestFocus();
                        }
                    } else if (FirstName.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.first_name), Toast.LENGTH_SHORT).show();
                        first_name.setError(getString(R.string.first_name));
                        first_name.requestFocus();
                    } else if (LastName.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.last_name), Toast.LENGTH_SHORT).show();
                        last_name.setError(getString(R.string.last_name));
                        last_name.requestFocus();
                    } else if (Mobile.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.mobileno), Toast.LENGTH_SHORT).show();
                        mobile.setError(getString(R.string.mobileno));
                        mobile.requestFocus();
                    } else if (Email.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.email_id), Toast.LENGTH_SHORT).show();
                        email.setError(getString(R.string.email_id));
                        email.requestFocus();
                    }
                /*else if(Location.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.location), Toast.LENGTH_SHORT).show();
                }*/
                    else if (Password.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                        password.setError(getString(R.string.password));
                        password.requestFocus();
                    }
                }
            });

            mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus)
                    {
                        if(!mobile.getText().toString().isEmpty())
                        {
                            verifyDuplicateMobile(mobile.getText().toString().trim());
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void verifyDuplicateMobile(String number)
    {
        String url = Constants.VerifyDuplicate + number;

        IOUtils ioUtils = new IOUtils();

        ioUtils.getGETStringRequest(RegisterActivity.this,url, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                verifyMobileResponse(result);
            }
        }, new IOUtils.VolleyFailureCallback() {
            @Override
            public void onFailure(String result) {

            }
        });
    }

    private void verifyMobileResponse(String Response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Response);
            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Found"))
            {
                duplicateMobile = false;
                mobile.setError("Mobile No. Already Registered");
                //Toast.makeText(this, "Mobile No. Already Registered", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(this)
                        .setTitleText("Mobile No. Already Registered")
                        .show();
            }
            else if(message.equalsIgnoreCase("Not Found"))
            {
                duplicateMobile = true;
            }

        }catch (Exception e)
        {

        }
    }

    public void verifyDuplicateMobileAndRegister(String number)
    {
        String url = Constants.VerifyDuplicate + number;

        IOUtils ioUtils = new IOUtils();

        ioUtils.getGETStringRequest(RegisterActivity.this,url, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                verifyMobileResponseAndRegister(result);
            }
        }, new IOUtils.VolleyFailureCallback() {
            @Override
            public void onFailure(String result) {

            }
        });
    }

    private void verifyMobileResponseAndRegister(String Response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Response);
            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Found"))
            {
                duplicateMobile = false;
                mobile.setError("Mobile No. Already Registered");
                //Toast.makeText(this, "Mobile No. Already Registered", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(this)
                        .setTitleText("Mobile No. Already Registered")
                        .show();
            }
            else if(message.equalsIgnoreCase("Not Found"))
            {
                duplicateMobile = true;
                sendRegisterData(FirstName, LastName, Mobile, Email, Referral_code, Password, Type, SocialID);
            }

        }catch (Exception e)
        {

        }
    }

    public void sendRegisterData(String FirstName, String LastName, String Mobile, String Email, String ReferralCode, String Password, String Type, String SocialID) {

        try
        {

            String url = Constants.RegisterActivityAPI;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("firstName", FirstName);
                postObject.put("lastName", LastName);
                postObject.put("mobileNo", Mobile);
                postObject.put("emailId", Email);
                //postObject.put("location",Location);
                postObject.put("referralCode", ReferralCode);
                postObject.put("socialId", SocialID);
                postObject.put("registrationType", Type);
                postObject.put("password", Password);

                jsonObject.put("post", postObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(RegisterActivity.this,url, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    btnRegister.setClickable(true);
                    RegisterResponse(result.toString());
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnRegister.setClickable(true);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void RegisterResponse(String Response) {
        try {

            loginMain = gson.fromJson(Response, LoginMain.class);

            if(loginMain.getUser().getOk().equalsIgnoreCase("true"))
            {

                SharedPrefUtil.setToken(RegisterActivity.this,loginMain.getUser().getToken());
                getProfileData(loginMain.getUser().getToken());
            }
            else if(loginMain.getUser().getOk().equalsIgnoreCase("false"))
            {
                //Toast.makeText(this, loginMain.getMessage(), Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(this)
                        .setTitleText(loginMain.getMessage())
                        .show();
            }

        } catch (Exception jse) {
            Log.i(TAG, jse.getMessage());
        }
    }

    private void getProfileData(String Token) {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + Token);

            ioUtils.getPOSTStringRequestHeader(RegisterActivity.this,Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);

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

    private void profileDataResponse(String Response)
    {
        try
        {

            SharedPrefUtil.setUser(getApplicationContext(), Response.toString());
            Intent startMainActivity = new Intent(getApplicationContext(), CheckVehicleActivity.class);
            startActivity(startMainActivity);

            finish();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try
        {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                            finish();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                            finish();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            /*if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }*/

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
