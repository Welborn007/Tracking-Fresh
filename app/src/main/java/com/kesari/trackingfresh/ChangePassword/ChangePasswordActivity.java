package com.kesari.trackingfresh.ChangePassword;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ChangePasswordActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    private Gson gson;
    MyApplication myApplication;

    EditText password,new_password,confirmPassword;
    FancyButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            gson = new Gson();

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(ChangePasswordActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            myApplication = (MyApplication) getApplicationContext();

            password = (EditText) findViewById(R.id.password);
            new_password = (EditText) findViewById(R.id.new_password);
            confirmPassword = (EditText) findViewById(R.id.confirmPassword);
            btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String passwordTxt = password.getText().toString().trim();
                    String new_passwordTxt = new_password.getText().toString().trim();
                    String confirmPasswordTxt = confirmPassword.getText().toString().trim();

                    if(!passwordTxt.isEmpty() && !new_passwordTxt.isEmpty() && !confirmPasswordTxt.isEmpty())
                    {
                        if(new_passwordTxt.equalsIgnoreCase(confirmPasswordTxt))
                        {
                            ResetPassword(passwordTxt,new_passwordTxt,confirmPasswordTxt);
                        }
                        else
                        {
                            confirmPassword.setError("Password Doesn't Match New Password!");
                        }
                    }
                    else if(passwordTxt.isEmpty())
                    {
                        password.setError(getString(R.string.passwordYour));
                        password.requestFocus();
                    }
                    else if(new_passwordTxt.isEmpty())
                    {
                        new_password.setError(getString(R.string.newPassword));
                        new_password.requestFocus();
                    }
                    else if(confirmPasswordTxt.isEmpty())
                    {
                        confirmPassword.setError(getString(R.string.confirmNewPassword));
                        confirmPassword.requestFocus();
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void ResetPassword(String oldPassword, String newPassword,String ConfirmPassword) {
        try {

            String url = Constants.changePassword;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("oldPassword", oldPassword);
                postObject.put("newPassword",newPassword);
                postObject.put("confirmPassword",ConfirmPassword);

                jsonObject.put("post", postObject);

                //Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ChangePasswordActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(ChangePasswordActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    //Log.d(TAG, result.toString());

                    ResetPasswordResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void ResetPasswordResponse(String Response)
    {
        try
        {
//            {"message":"Password Not Matched"}

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Password Changed Successfull"))
            {
                DashboardActivity.LogOutFunc(ChangePasswordActivity.this);
                Toast.makeText(ChangePasswordActivity.this, "Password Changed!!", Toast.LENGTH_SHORT).show();
            }
            else if(message.equalsIgnoreCase("Password Not Matched"))
            {
                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
