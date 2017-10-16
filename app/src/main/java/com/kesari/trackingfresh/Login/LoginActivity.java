package com.kesari.trackingfresh.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.kesari.trackingfresh.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.kesari.trackingfresh.ForgetPassword.ForgotPassword_Activity;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Register.RegisterActivity;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.ErrorPOJO;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, NetworkUtilsReceiver.NetworkResponseInt {

    Button btnLogin, btnSignup;
    TextView btnForget;
//    ImageView Fbbtn, Googlebtn;
    private NetworkUtilsReceiver networkUtilsReceiver;
    ErrorPOJO errorPOJO;
    private String TAG = this.getClass().getSimpleName();

    SharedPreferences sharedpreferencesLogin;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";

    //Google
    SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN = 9001;
    Dialog dialog;
    User user;

    //Facebook
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    String fb_id, fb_name, fb_gender, fb_birthday, fb_email;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean permission = false;
    EditText user_name, password;
    private Boolean exit = false;
    private Gson gson;
    LoginMain loginMain;
    Button faceBookLogin,googleLogin;
    TextView signupTextView,toolbarTitleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        try {

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();
            //printHashKey(LoginActivity.this);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnSignup = (Button) findViewById(R.id.btnSignup);
            btnForget = (TextView) findViewById(R.id.btnForget);
            signupTextView = (TextView) findViewById(R.id.signupTextView);

            user_name = (EditText) findViewById(R.id.user_name);
            password = (EditText) findViewById(R.id.password);


          /*  password.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here

                            password.setFocusable(true);
                            password.requestFocus();

                            if(password.getTransformationMethod() == PasswordTransformationMethod.getInstance())
                            {
                                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }
                            else if(password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance())
                            {
                                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            }

                            return true;
                        }
                    }
                    return false;
                }
            });*/

            sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);

            String token = sharedpreferencesLogin.getString("token", "None");
            Log.i("Firebasetoken", token);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String UserName = user_name.getText().toString();
                    String Password = password.getText().toString();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (!UserName.isEmpty() && !Password.isEmpty()) {
                        if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {

                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("No internet access")
                                    .setContentText("Check Settings!")
                                    .setConfirmText("Settings")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        }
                                    })
                                    .show();

                          /*  FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });
                            return;*/
                        } else {
                            btnLogin.setClickable(false);
                            sendSimpleLoginData(UserName, Password);
                        }
                    } else if (UserName.isEmpty()) {
                        //Toast.makeText(LoginActivity.this, getString(R.string.mobileno), Toast.LENGTH_SHORT).show();
                        user_name.setError(getString(R.string.mobileno));
                    } else if (Password.isEmpty()) {
                        //Toast.makeText(LoginActivity.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                        password.setError(getString(R.string.password));
                    }

                }
            });

            signupTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startMainActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                    startMainActivity.putExtra("Type", "simple");
                    startActivity(startMainActivity);
                }
            });

            btnForget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startMainActivity = new Intent(getApplicationContext(), ForgotPassword_Activity.class);
                    startActivity(startMainActivity);
                }
            });

            if (Build.VERSION.SDK_INT >= 23) {

                if (checkAndRequestPermissions()) {

                    final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        IOUtils.buildAlertMessageNoGps(LoginActivity.this);
                    } else {
                        if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                            // LOCATION SERVICE
                            startService(new Intent(this, LocationServiceNew.class));
                            Log.e(TAG, "Location service is already running");
                        }
                    }

                } else {

                }

                // Marshmallow+
                //permissionCheck();

            }

            init();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void sendSimpleLoginData(String Mobile, String Password) {

        try
        {

            //String url = "http://192.168.1.10:8000/api/customer/login";
            String url = Constants.LoginActivityAPI;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("mobileNo", Mobile);
                postObject.put("password", Password);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(LoginActivity.this, url, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SimpleResponse(result.toString());
                    btnLogin.setClickable(true);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnLogin.setClickable(true);
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void SimpleResponse(String Response) {
        try {

            loginMain = gson.fromJson(Response, LoginMain.class);

            if(loginMain.getUser().getOk().equalsIgnoreCase("true"))
            {

                SharedPrefUtil.setToken(LoginActivity.this,loginMain.getUser().getToken());
                getProfileData(loginMain.getUser().getToken());
            }
            else if(loginMain.getUser().getOk().equalsIgnoreCase("false"))
            {
                //Toast.makeText(this, loginMain.getMessage(), Toast.LENGTH_SHORT).show();
                new SweetAlertDialog(this)
                        .setTitleText(loginMain.getMessage())
                        .show();

                user_name.setText("");
                password.setText("");
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

            ioUtils.getPOSTStringRequestHeader(LoginActivity.this,Constants.Profile, params, new IOUtils.VolleyCallback() {
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

    public void sendSocialLoginData(final String SocialID, final String Name, final String Email, final String Type, final String firstname, final String lastname) {

        try
        {

            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.progressdialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            String url = Constants.LoginActivityAPI;

            //Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("socialId", SocialID);
                postObject.put("registrationType",Type);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(LoginActivity.this, url, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SocialResponse(result.toString(), SocialID, Name, Email, Type, firstname, lastname);
                }
            });*/

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", response.toString());
                            dialog.dismiss();
                            SocialResponse(response.toString(), SocialID, Name, Email, Type, firstname, lastname);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //VolleyLog.d("Error", "Error: " + error.getMessage());
                    dialog.dismiss();

                    try{
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        json = new String(response.data);
                        Log.d("Error", json);

                        ErrorResponse(json,LoginActivity.this, SocialID, Name, Email, Type, firstname, lastname);

                    }catch (Exception e)
                    {
                        //Log.d("Error", e.getMessage());
                    }
                }
            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //Adding request to request queue
            MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void ErrorResponse(String Response,Context context,String SocialID, String Name, String Email, String Type, String firstname, String lastname)
    {
        gson = new Gson();
        errorPOJO = gson.fromJson(Response, ErrorPOJO.class);

        if(errorPOJO.getErrors() != null)
        {
            String[] error = errorPOJO.getErrors();
            String errorString = error[0];

            if(errorString.equalsIgnoreCase("User not found"))
            {
                sendRegisterData(firstname, lastname,  Email, Type, SocialID);
            }

            //FireToast.customSnackbar(context, errorString,"");

        }
        else if(errorPOJO.getMessage() != null)
        {
            //FireToast.customSnackbar(context, errorPOJO.getMessage(),"");

            /*new SweetAlertDialog(context)
                    .setTitleText(errorPOJO.getMessage())
                    .show();*/

            if(errorPOJO.getMessage().equalsIgnoreCase("User not found"))
            {
                sendRegisterData(firstname, lastname,  Email, Type, SocialID);
            }
        }
        else
        {
            //FireToast.customSnackbar(context, "Oops Something Went Wrong!!","");

            new SweetAlertDialog(context)
                    .setTitleText("Oops Something Went Wrong!!")
                    .show();
        }


    }

    public void SocialResponse(String Response, String SocialID, String Name, String Email, String Type, String firstname, String lastname) {
        try {

            loginMain = gson.fromJson(Response, LoginMain.class);

            if(loginMain.getUser().getOk().equalsIgnoreCase("true"))
            {
                /*Intent startMainActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(startMainActivity);

                finish();*/

                SharedPrefUtil.setToken(LoginActivity.this,loginMain.getUser().getToken());
                getProfileData(loginMain.getUser().getToken());
            }


        } catch (Exception jse) {
            Log.i(TAG, jse.getMessage());
        }
    }

    public void sendRegisterData(String FirstName, String LastName, String Email, String Type, String SocialID) {

        try
        {

            //String url = "http://192.168.1.10:8000/api/customer/";

            String url = Constants.RegisterActivityAPI;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("firstName", FirstName);
                postObject.put("lastName", LastName);
                //postObject.put("mobileNo", Mobile);
                postObject.put("emailId", Email);
                //postObject.put("location",Location);
                //postObject.put("referralCode", ReferralCode);
                postObject.put("socialId", SocialID);
                postObject.put("registrationType", Type);
                //postObject.put("password", Password);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(LoginActivity.this, url, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    RegisterResponse(result.toString());
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

    public void RegisterResponse(String Response) {
        try {

            loginMain = gson.fromJson(Response, LoginMain.class);

            if(loginMain.getUser().getOk().equalsIgnoreCase("true"))
            {
                /*Intent startMainActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(startMainActivity);

                finish();*/

                SharedPrefUtil.setToken(LoginActivity.this,loginMain.getUser().getToken());
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


    @Override
    protected void onRestart() {
        super.onRestart();

        if (permission) {
            Log.i("permission", "backPressed");
            if (checkAndRequestPermissions()) {
                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    IOUtils.buildAlertMessageNoGps(LoginActivity.this);
                } else {
                    if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                        // LOCATION SERVICE
                        startService(new Intent(this, LocationServiceNew.class));
                        Log.e(TAG, "Location service is already running");
                    }
                }
            }
        }

    }

    private boolean checkAndRequestPermissions() {
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int Camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }

        if (Camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        String TAG = "PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "All permission granted");
                        Toast.makeText(getApplicationContext(), "All permission granted", Toast.LENGTH_LONG).show();

                        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            IOUtils.buildAlertMessageNoGps(LoginActivity.this);
                        } else {
                            if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                                // LOCATION SERVICE
                                startService(new Intent(this, LocationServiceNew.class));
                                Log.e(TAG, "Location service is already running");
                            }
                        }

                        //showDeviceDetails();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                            if (checkAndRequestPermissions()) {
                                // carry on the normal flow, as the case of  permissions  granted.

                                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    IOUtils.buildAlertMessageNoGps(LoginActivity.this);
                                } else {
                                    if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                                        // LOCATION SERVICE
                                        startService(new Intent(this, LocationServiceNew.class));
                                        Log.e(TAG, "Location service is already running");
                                    }
                                }
                            }
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            //Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            //proceed with logic by disabling the related features or quit the app.
                            showMessageForNeverAskAgain("Some core functionalities of the app might not work correctly without these permission. Go to settings and enable these for " + getString(R.string.app_name),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getPackageName(), null)));

                                            permission = true;
                                        }
                                    }
                            );
                        }
                    }
                }
            }
        }
    }

    private void showMessageForNeverAskAgain(String message, DialogInterface.OnClickListener settingsListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Permission Necessary")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Settings", settingsListener)
                .create()
                .show();
    }

    private void init() {

        try
        {

            //Google
            googleLogin = (Button) findViewById(R.id.googleLogin);
            googleLogin.setOnClickListener(this);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, LoginActivity.this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setScopes(gso.getScopeArray());
            findViewById(R.id.sign_in_button).setOnClickListener(this);


            //Facebook
            faceBookLogin = (Button) findViewById(R.id.faceBookLogin);
            faceBookLogin.setOnClickListener(this);
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("public_profile", "email", "user_friends", "user_birthday");
            callbackManager = CallbackManager.Factory.create();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        try
        {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                //showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        hideProgressDialog();
                        handleSignInResult(googleSignInResult);
                    }
                });
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.googleLogin:

                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                    /*FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;*/

                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("No Internet access")
                            .setContentText("Check Settings!")
                            .setConfirmText("Settings")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .show();
                } else {
                    signIn();
                }

                break;

            case R.id.faceBookLogin:

                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                    /*FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;*/
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("No Internet access")
                            .setContentText("Check Settings!")
                            .setConfirmText("Settings")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .show();
                } else {
                    if (isLoggedIn()) {
                        LoginManager.getInstance().logOut();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.performClick();
                                loginButton.setPressed(true);
                                loginButton.invalidate();
                                loginButton.registerCallback(callbackManager, mCallBack);
                                loginButton.setPressed(false);
                                loginButton.invalidate();
                            }
                        }, 2000);
                    } else {
                        loginButton.performClick();
                        loginButton.setPressed(true);
                        loginButton.invalidate();
                        loginButton.registerCallback(callbackManager, mCallBack);
                        loginButton.setPressed(false);
                        loginButton.invalidate();
                    }
                }
                break;


        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Facebook Call response
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            try {
                                user = new User();

                                String id = "";
                                String name = "";
                                String email = "";
                                String firstname = "";
                                String lastname = "";
                                String registration_Type = "facebook";

                                if(object.has("id"))
                                {
                                    user.ID = object.getString("id").toString();
                                    id = object.getString("id").toString();
                                }

                                if(object.has("email"))
                                {
                                    user.email = object.getString("email").toString();
                                    email = object.getString("email").toString();
                                }

                                if(object.has("name"))
                                {
                                    user.name = object.getString("name").toString();
                                    name = object.getString("name").toString();
                                }

                                if(object.has("gender"))
                                {
                                    user.gender = object.getString("gender").toString();
                                }

                                if(object.has("first_name"))
                                {
                                    firstname = object.getString("first_name").toString();
                                }

                                if(object.has("last_name"))
                                {
                                    lastname = object.getString("last_name").toString();
                                }


                                user.createdAt = "1";
                                /*user.birthday = object.getString("birthday").toString();*/
                                user.profileImageUrl = new URL("https://graph.facebook.com/" + object.getString("id").toString() + "/picture?type=large").toString();

                               /* startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                finish();*/



                                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                                    /*FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                                        @Override
                                        public void onActionClicked(Snackbar snackbar) {
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        }
                                    });
                                    return;*/

                                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("No internet access")
                                            .setContentText("Check Settings!")
                                            .setConfirmText("Settings")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                                }
                                            })
                                            .show();

                                } else {

                                    if(!id.isEmpty() && !email.isEmpty() && !firstname.isEmpty() && !lastname.isEmpty())
                                    {
                                        sendSocialLoginData(id, name, email, registration_Type, firstname, lastname);
                                        //Toast.makeText(LoginActivity.this, "All Details Present", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        verifySocialLoginDetails(firstname,lastname,email,id,name,registration_Type);
                                    }

                                }

                                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                                editor.putString("name", object.getString("name").toString());
                                editor.commit();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday, first_name, last_name");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            //Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            //Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifySocialLoginDetails(final String FirstName, String LastName, String Email, final String ID, final String Name, final String Registration_Type) {
        try {

            // Create custom dialog object
            dialog = new Dialog(LoginActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.confirm_social_login_details);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            final EditText first_name,last_name,email;
            FancyButton confirmNumber;

            first_name = (EditText) dialog.findViewById(R.id.first_name);
            last_name = (EditText) dialog.findViewById(R.id.last_name);
            email = (EditText) dialog.findViewById(R.id.email);

            confirmNumber = (FancyButton) dialog.findViewById(R.id.confirmNumber);

            first_name.setText(FirstName);
            last_name.setText(LastName);
            email.setText(Email);

            confirmNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String First_Name = first_name.getText().toString().trim();
                    String Last_Name = last_name.getText().toString().trim();
                    String Email = email.getText().toString().trim();

                    if (!First_Name.isEmpty() && !Last_Name.isEmpty() && !Email.isEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                            sendSocialLoginData(ID, Name, Email, Registration_Type, First_Name, Last_Name);
                            //Toast.makeText(LoginActivity.this, "Data Sent Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            email.setError(getString(R.string.proper_email));
                        }
                    }
                    else if(First_Name.isEmpty())
                    {
                        first_name.setError(getString(R.string.first_name));
                    }
                    else if(Last_Name.isEmpty())
                    {
                        first_name.setError(getString(R.string.first_name));
                    }
                    else if(Email.isEmpty())
                    {
                        email.setError(getString(R.string.first_name));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try
        {
            //Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);

            //Google Callback
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        try
        {

            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
            /*mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));*/
                Log.i("email", acct.getEmail());
                Log.i("id", acct.getId());
                Log.i("name", acct.getDisplayName());
            /*Log.i("photo",acct.getPhotoUrl().toString());*/
//            Log.i("authcode",acct.getServerAuthCode());
           /* Log.i("scopes",acct.getGrantedScopes().toString());*/

                user = new User();
                user.ID = acct.getId();
                user.email = acct.getEmail();
                user.name = acct.getDisplayName();
                user.setFirstName(acct.getGivenName());
                user.setLastName(acct.getFamilyName());
                user.createdAt = "3";
//            user.profileImageUrl = acct.getPhotoUrl().toString();

            /*Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();*/

                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();

                Log.i(personFamilyName, personGivenName);

                String id = acct.getId();
                String name = acct.getDisplayName();
                String email = acct.getEmail();
                String registration_Type = "google";

                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                   /* FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;*/

                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("No internet access")
                            .setContentText("Check Settings!")
                            .setConfirmText("Settings")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .show();
                } else {
                    sendSocialLoginData(id, name, email, registration_Type, personGivenName, personFamilyName);
                }

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

                SharedPreferences.Editor editor = pref.edit();

                editor.putString("name", String.valueOf(acct.getDisplayName()));
                editor.commit();

            /*updateUI(true);*/
            } else {
                // Signed out, show unauthenticated UI.
           /* updateUI(false);*/
           /* signIn();*/
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
    // [END handleSignInResult]


    private void showProgressDialog() {

        try {

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.loading));
                mProgressDialog.setIndeterminate(true);
            }

            mProgressDialog.show();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.kesari.trackingfresh", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    private void hideProgressDialog() {

        try
        {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    // [START signIn]
    private void signIn() {
        try
        {

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        /*updateUI(false);*/
                        // [END_EXCLUDE]

                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        /*updateUI(false);*/
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void NetworkOpen() {

    }

    @Override
    public void NetworkClose() {
        if (!NetworkUtils.isNetworkConnectionOn(this)) {
            /*FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            return;*/

            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No internet access")
                    .setContentText("Check Settings!")
                    .setConfirmText("Settings")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .show();
        }
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
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

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
