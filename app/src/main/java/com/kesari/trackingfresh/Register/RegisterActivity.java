package com.kesari.trackingfresh.Register;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    EditText first_name,last_name,mobile,email,location,referral_code,password;
    Button btnRegister;
    private static final String TAG = "Register_Call";

    String SocialID = "",Name,Email,Type = "simple";
    GoogleApiClient mGoogleApiClient;
    private NetworkUtilsReceiver networkUtilsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);
        referral_code = (EditText) findViewById(R.id.referral_code);
        password = (EditText) findViewById(R.id.password);

        try
        {
            SocialID = getIntent().getStringExtra("SocialID");
            Name = getIntent().getStringExtra("Name");
            Email = getIntent().getStringExtra("Email");
            Type = getIntent().getStringExtra("Type");

            first_name.setText(Name);
            email.setText(Email);

        }catch (NullPointerException npe)
        {
            Log.i("Null","Null");
            SocialID = "";
            Type = "simple";
        }

        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FirstName = first_name.getText().toString();
                String LastName = last_name.getText().toString();
                String Mobile = mobile.getText().toString();
                String Email = email.getText().toString();
                //String Location = location.getText().toString();
                String Referral_code = referral_code.getText().toString();
                String Password = password.getText().toString();

                if(!FirstName.isEmpty() && !LastName.isEmpty() && !Mobile.isEmpty() && !Email.isEmpty() && !Password.isEmpty())
                {
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches() && android.util.Patterns.PHONE.matcher(Mobile).matches())
                    {
                        //int mob = Integer.parseInt(Mobile);

                        if(Mobile.length() >= 10)
                        {
                            if (!NetworkUtils.isNetworkConnectionOn(RegisterActivity.this)) {
                                FireToast.customSnackbarWithListner(RegisterActivity.this, "No internet access", "Settings", new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });
                                return;
                            }
                            else
                            {
                                sendRegisterData(FirstName,LastName,Mobile,Email,Referral_code,Password,Type,SocialID);
                            }
                        }
                        else
                        {
                            //Toast.makeText(RegisterActivity.this, getString(R.string.less_than_10digit) , Toast.LENGTH_SHORT).show();
                            mobile.setError(getString(R.string.less_than_10digit));
                        }
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                    {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.proper_email), Toast.LENGTH_SHORT).show();
                        email.setError(getString(R.string.proper_email));
                    }
                    else if(!android.util.Patterns.PHONE.matcher(Mobile).matches())
                    {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.proper_mobile), Toast.LENGTH_SHORT).show();
                        mobile.setError(getString(R.string.proper_mobile));
                    }
                }
                else if(FirstName.isEmpty())
                {
                    //Toast.makeText(RegisterActivity.this, getString(R.string.first_name), Toast.LENGTH_SHORT).show();
                    first_name.setError(getString(R.string.first_name));
                }
                else if(LastName.isEmpty())
                {
                    //Toast.makeText(RegisterActivity.this, getString(R.string.last_name), Toast.LENGTH_SHORT).show();
                    last_name.setError(getString(R.string.last_name));
                }
                else if(Mobile.isEmpty())
                {
                    //Toast.makeText(RegisterActivity.this, getString(R.string.mobileno), Toast.LENGTH_SHORT).show();
                    mobile.setError(getString(R.string.mobileno));
                }
                else if(Email.isEmpty())
                {
                    //Toast.makeText(RegisterActivity.this, getString(R.string.email_id), Toast.LENGTH_SHORT).show();
                    email.setError(getString(R.string.email_id));
                }
                /*else if(Location.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, getString(R.string.location), Toast.LENGTH_SHORT).show();
                }*/
                else if(Password.isEmpty())
                {
                    //Toast.makeText(RegisterActivity.this, getString(R.string.password), Toast.LENGTH_SHORT).show();
                    password.setError(getString(R.string.password));
                }
            }
        });

    }


    public void sendRegisterData(String FirstName,String LastName,String Mobile,String Email,String ReferralCode,String Password,String Type,String SocialID){

        //String url = "http://192.168.1.10:8000/api/customer/";

        String url = Constants.RegisterActivityAPI;

        Log.i("url",url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("firstName",FirstName);
            postObject.put("lastName",LastName);
            postObject.put("mobileNo",Mobile);
            postObject.put("emailId",Email);
            //postObject.put("location",Location);
            postObject.put("referralCode",ReferralCode);
            postObject.put("socialId",SocialID);
            postObject.put("registrationType",Type);
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

                        RegisterResponse(response.toString());

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

    public void RegisterResponse(String Response)
    {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            String status = jsonObject.getString("status");
            //String message = jsonObject.getString("message");

            if(status.equalsIgnoreCase("500"))
            {
                JSONArray jsonArray = jsonObject.getJSONArray("errors");
                String errors = jsonArray.getString(0);
                Toast.makeText(this, errors, Toast.LENGTH_LONG).show();
            }

            if(status.equalsIgnoreCase("200"))
            {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                finish();
                                Toast.makeText(RegisterActivity.this, getString(R.string.user_registered), Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(i);
                            }
                        });
            }

        }catch (JSONException jse)
        {
            Log.i("Exception",jse.getMessage());
        }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        finish();
                        Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                });
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
                            Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(i);
                        }
                    });

            return true;
        }

        return super.onOptionsItemSelected(item);
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
