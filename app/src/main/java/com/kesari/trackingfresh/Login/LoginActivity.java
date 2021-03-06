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
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.kesari.trackingfresh.ProductPage.DashboardActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Register.RegisterActivity;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,NetworkUtilsReceiver.NetworkResponseInt{

    Button btnLogin,btnSignup;
    ImageView Fbbtn,Googlebtn;
    private NetworkUtilsReceiver networkUtilsReceiver;

    private static final String TAG = "LoginActivity";

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
    String fb_id,fb_name,fb_gender,fb_birthday,fb_email;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean permission = false;

    EditText user_name,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);

        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String UserName = user_name.getText().toString();
                String Password = password.getText().toString();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if(!UserName.isEmpty() && !Password.isEmpty())
                {
                    if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                        FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        return;
                    }
                    else
                    {
                        sendSimpleLoginData(UserName,Password);
                    }
                }
                else if(UserName.isEmpty())
                {
                    //Toast.makeText(LoginActivity.this, getString(R.string.mobileno), Toast.LENGTH_SHORT).show();
                    user_name.setError(getString(R.string.mobileno));
                }
                else if(Password.isEmpty())
                {
                    //Toast.makeText(LoginActivity.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                    password.setError(getString(R.string.password));
                }

            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMainActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startMainActivity.putExtra("Type","simple");
                startActivity(startMainActivity);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {

            if(checkAndRequestPermissions())
            {

            }
            else
            {

            }

            // Marshmallow+
            //permissionCheck();

        }

        init();
    }

    public void sendSimpleLoginData(String Mobile,String Password){

        //String url = "http://192.168.1.10:8000/api/customer/login";
        String url = Constants.LoginActivityAPI;

        Log.i("url",url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobileNo",Mobile);
            postObject.put("password",Password);

            jsonObject.put("post",postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // pDialog.hide();

                        SimpleResponse(response.toString());

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                //pDialog.hide();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, TAG);

    }

    public void SimpleResponse(String Response)
    {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            String status = jsonObject.getString("status");

            if(status.equalsIgnoreCase("404"))
            {
                Toast.makeText(this, getString(R.string.please_register), Toast.LENGTH_SHORT).show();
                user_name.setText("");
                password.setText("");
            }

            if(status.equalsIgnoreCase("500"))
            {
                String message = jsonObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                user_name.setText("");
                password.setText("");
            }

            if(status.equalsIgnoreCase("200"))
            {
                Intent startMainActivity = new Intent(getApplicationContext(),DashboardActivity.class);
                startActivity(startMainActivity);

                finish();

                SharedPrefUtil.setUser(getApplicationContext(), Response.toString());
            }

        }catch (JSONException jse)
        {

        }
    }

    public void sendSocialLoginData(final String SocialID, final String Name, final String Email, final String Type){

        //String url = "http://192.168.1.10:8000/api/customer/login";
        String url = Constants.LoginActivityAPI;

        Log.i("url",url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("socialId",SocialID);

            jsonObject.put("post",postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // pDialog.hide();

                        SocialResponse(response.toString(),SocialID,Name,Email,Type);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                //pDialog.hide();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, TAG);

    }

    public void SocialResponse(String Response,String SocialID,String Name,String Email,String Type)
    {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            String status = jsonObject.getString("status");

            if(status.equalsIgnoreCase("404"))
            {
                Intent startMainActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startMainActivity.putExtra("SocialID",SocialID);
                startMainActivity.putExtra("Name",Name);
                startMainActivity.putExtra("Email",Email);
                startMainActivity.putExtra("Type",Type);
                startActivity(startMainActivity);
            }

            if(status.equalsIgnoreCase("500"))
            {
                String message = jsonObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                user_name.setText("");
                password.setText("");
            }

            if(status.equalsIgnoreCase("200"))
            {
                Intent startMainActivity = new Intent(getApplicationContext(),DashboardActivity.class);
                startActivity(startMainActivity);

                finish();

                SharedPrefUtil.setUser(getApplicationContext(), Response.toString());
            }

        }catch (JSONException jse)
        {

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(permission)
        {
            Log.i("permission","backPressed");
            if(checkAndRequestPermissions())
            {

            }
        }

    }

    private  boolean checkAndRequestPermissions() {
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (readSMS != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
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

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, "All permission granted");
                        Toast.makeText(getApplicationContext(),"All permission granted",Toast.LENGTH_LONG).show();
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
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS))
                        {
                            if(checkAndRequestPermissions()) {
                                // carry on the normal flow, as the case of  permissions  granted.
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

    private void init()
    {
        //Google
        Googlebtn = (ImageView) findViewById(R.id.Googlebtn);
        Googlebtn.setOnClickListener(this);
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
        Fbbtn = (ImageView) findViewById(R.id.Fbbtn);
        Fbbtn.setOnClickListener(this);
        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email","user_friends","user_birthday");
        callbackManager= CallbackManager.Factory.create();
    }

    @Override
    public void onStart() {
        super.onStart();

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
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Googlebtn:

                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                    FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;
                }
                else
                {
                    signIn();
                }

                break;

            case R.id.Fbbtn:

                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                    FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    return;
                }
                else
                {
                    loginButton.performClick();
                    loginButton.setPressed(true);
                    loginButton.invalidate();
                    loginButton.registerCallback(callbackManager, mCallBack);
                    loginButton.setPressed(false);
                    loginButton.invalidate();
                }
                break;


        }
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
                                user.ID = object.getString("id").toString();
                                user.email = object.getString("email").toString();
                                user.name = object.getString("name").toString();
                                user.gender = object.getString("gender").toString();
                                user.createdAt = "1";
                                /*user.birthday = object.getString("birthday").toString();*/
                                user.profileImageUrl = new URL("https://graph.facebook.com/" + object.getString("id").toString() + "/picture?type=large").toString();

                               /* startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                finish();*/

                               String id = object.getString("id").toString();
                               String name =  object.getString("name").toString();
                               String email =  object.getString("email").toString();
                               String registration_Type = "facebook";

                                if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                                    FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                                        @Override
                                        public void onActionClicked(Snackbar snackbar) {
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        }
                                    });
                                    return;
                                }
                                else
                                {
                                    sendSocialLoginData(id,name,email,registration_Type);
                                }

                                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                                editor.putString("name", object.getString("name").toString());
                                editor.commit();

                            }catch (Exception e){
                                e.printStackTrace();
                            }


                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Facebook Callback
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google Callback
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            /*mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));*/
            Log.i("email",acct.getEmail());
            Log.i("id",acct.getId());
            Log.i("name",acct.getDisplayName());
            /*Log.i("photo",acct.getPhotoUrl().toString());*/
//            Log.i("authcode",acct.getServerAuthCode());
           /* Log.i("scopes",acct.getGrantedScopes().toString());*/

            user = new User();
            user.ID = acct.getId();
            user.email = acct.getEmail();
            user.name = acct.getDisplayName();
            user.createdAt = "3";
//            user.profileImageUrl = acct.getPhotoUrl().toString();

            /*Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();*/

            String id = acct.getId();
            String name =  acct.getDisplayName();
            String email =  acct.getEmail();
            String registration_Type = "google";

            if (!NetworkUtils.isNetworkConnectionOn(LoginActivity.this)) {
                FireToast.customSnackbarWithListner(LoginActivity.this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }
            else
            {
                sendSocialLoginData(id,name,email,registration_Type);
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
    }
    // [END handleSignInResult]


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
            FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkUtilsReceiver);
    }
}
