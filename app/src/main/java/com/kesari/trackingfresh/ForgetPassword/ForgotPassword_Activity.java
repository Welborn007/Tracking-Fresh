package com.kesari.trackingfresh.ForgetPassword;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ForgotPassword_Activity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    private EditText mobile,email;
    private FancyButton btnSubmit;
    private String input;

    boolean mobileBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password_);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Forget Password");
        toolbar.setTitleTextColor(ContextCompat.getColor(ForgotPassword_Activity.this,R.color.black));

        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.email);
        btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);


        mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                email.setText("");

                mobileBoolean = true;
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mobile.setText("");

                mobileBoolean = false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mobileBoolean)
                {
                    input = mobile.getText().toString().trim();
                }
                else
                {
                    input = email.getText().toString().trim();
                }

                if (!input.isEmpty())
                {
                    if(mobileBoolean)
                    {
                        if (android.util.Patterns.PHONE.matcher(input).matches()) {
                            sendData(input);
                        }
                        else
                        {
                            mobile.setError(getString(R.string.proper_mobile));
                        }
                    }
                    else
                    {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                            sendData(input);
                        }
                        else
                        {
                            email.setError(getString(R.string.proper_email));
                        }
                    }

                    //Toast.makeText(ForgotPassword_Activity.this, "Sent", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(ForgotPassword_Activity.this, "Enter mobile or email!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        try {

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                IOUtils.buildAlertMessageNoGps(ForgotPassword_Activity.this);
            } else {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void sendData(String filter){

        try
        {

            String url = Constants.ForgetPassword ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                if(mobileBoolean)
                {
                    postObject.put("mobileNo",filter);
                }
                else
                {
                    postObject.put("email",filter);
                }

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ForgotPassword_Activity.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(ForgotPassword_Activity.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.i(TAG+" RESPONSE",result);

                    ForgotPasswordResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void ForgotPasswordResponse(String Response)
    {
        //{"message":"OTP Send On Email :welborn@kesari.in"}

        try
        {
            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message != null)
            {
                if(!message.isEmpty())
                {
                   mobile.setText("");
                    email.setText("");
                    mobileBoolean = false;
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Sorry request failed!!!", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
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

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
