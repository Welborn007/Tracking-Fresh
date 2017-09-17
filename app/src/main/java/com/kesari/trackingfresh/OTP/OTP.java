package com.kesari.trackingfresh.OTP;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
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
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class OTP extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private NetworkUtilsReceiver networkUtilsReceiver;
    Button sendOTP;//send, skip, resend,call;
    EditText Otp1,Otp2,Otp3,Otp4;
    private String TAG = this.getClass().getSimpleName();
    Dialog dialog;

    TextView counter,number;
    String mobile,username;

    private Gson gson;

    IntentFilter filter1;
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    SendOtpPOJO sendOtpPOJO;
    public static final String SMS_RECEIVED_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String SMS_RECEIVED_ACTION1 = "android.provider.Telephony.SMS_RECEIVED";

    TextView callMeTextView,resendSmsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otp);

        try
        {

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(OTP.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            sendOTP = (Button) findViewById(R.id.sendOTP);
//            skip = (FancyButton) findViewById(R.id.skip);

            Otp1 = (EditText) findViewById(R.id.otp1);
            Otp2 = (EditText) findViewById(R.id.otp2);
            Otp3 = (EditText) findViewById(R.id.otp3);
            Otp4 = (EditText) findViewById(R.id.otp4);

            mobile = getIntent().getStringExtra("mobile_num");

            Otp1.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!Otp1.getText().toString().isEmpty())
                    {
                        Otp2.requestFocus();
                    }
                }
            });

            Otp2.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!Otp2.getText().toString().isEmpty())
                    {
                        Otp3.requestFocus();
                    }
                    else
                    {
                        Otp1.requestFocus();
                    }
                }
            });

            Otp3.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!Otp3.getText().toString().isEmpty())
                    {
                        Otp4.requestFocus();
                    }
                    else
                    {
                        Otp2.requestFocus();
                    }
                }
            });

            Otp4.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!Otp4.getText().toString().isEmpty())
                    {

                    }
                    else
                    {
                        Otp3.requestFocus();
                    }
                }
            });

            resendSmsTextView = (TextView) findViewById(R.id.resendSmsTextView);
            callMeTextView = (TextView) findViewById(R.id.callMeTextView);
            counter = (TextView) findViewById(R.id.counter);
            number = (TextView) findViewById(R.id.mobinumber);

            number.setText(mobile);

            Timer();

            resendSmsTextView.setEnabled(false);
            callMeTextView.setEnabled(false);

            resendSmsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMobileNumber(mobile);
                    resendSmsTextView.setEnabled(false);
                    callMeTextView.setEnabled(false);
                }
            });

            callMeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callVoiceOTP(mobile);
                    callMeTextView.setEnabled(false);
                    callMeTextView.setEnabled(false);
                }
            });

           /* skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OTP.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
*/
            sendOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String OTP1 = Otp1.getText().toString().trim();
                    String OTP2 = Otp2.getText().toString().trim();
                    String OTP3 = Otp3.getText().toString().trim();
                    String OTP4 = Otp4.getText().toString().trim();

                    String OTPNumber = OTP1 + OTP2 + OTP3 + OTP4;

                    if(!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty())
                    {
                        matchOTPNumber(mobile, OTPNumber);
                    }
                    else
                    {
                        //FireToast.customSnackbar(OTP.this, "Enter OTP!!","");

                        new SweetAlertDialog(OTP.this)
                                .setTitleText("Enter OTP!!")
                                .show();
                    }

                }
            });

            filter1 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(myReceiver, filter1);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void matchOTPNumber(String MobileNo,String OTP)
    {
        try
        {

            String url = Constants.MatchOTP ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("otp", OTP);
                postObject.put("mobileNo", MobileNo);
                postObject.put("id", SharedPrefUtil.getUser(OTP.this).getData().get_id());

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OTP.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(OTP.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    matchOTPResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void matchOTPResponse(String Response)
    {
        try
        {

        JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(Message.equalsIgnoreCase("Otp Matched"))
            {
                Intent intent = new Intent(OTP.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                //FireToast.customSnackbar(OTP.this,Message,"");

                new SweetAlertDialog(OTP.this)
                        .setTitleText("")
                        .show();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void Timer()
    {
        try
        {

            new CountDownTimer(61000, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {
                    counter.setText("OTP will be received within : " + String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    counter.setText("click on 'Resend OTP'");
                    resendSmsTextView.setEnabled(true);
                    callMeTextView.setEnabled(true);
                }
            }.start();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Retrieves a map of extended data from the intent.
            Bundle bundle = intent.getExtras();

            Log.i("Receiver","Started");

            if (intent.getAction().equals(SMS_RECEIVED_ACTION1)) {
                try {

                    if (bundle != null) {

                        final Object[] pdusObj = (Object[]) bundle.get("pdus");

                        for (int i = 0; i < pdusObj.length; i++) {

                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                            String senderNum = phoneNumber;
                            String message = currentMessage.getDisplayMessageBody();

                            Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                            if(message.contains("Tracking Fresh One Time Password"))
                            {
                                String s = message.replaceAll("[^0-9]", "");

                                number.setText(senderNum);

                                if(!s.isEmpty())
                                {
                                    Otp1.setText(String.valueOf(s.charAt(0)));
                                    Otp2.setText(String.valueOf(s.charAt(1)));
                                    Otp3.setText(String.valueOf(s.charAt(2)));
                                    Otp4.setText(String.valueOf(s.charAt(3)));

                                    String OTPNumber = String.valueOf(s.charAt(0)) + String.valueOf(s.charAt(1)) + String.valueOf(s.charAt(2)) + String.valueOf(s.charAt(3));

                                    if(!String.valueOf(s.charAt(0)).isEmpty() && !String.valueOf(s.charAt(1)).isEmpty() && !String.valueOf(s.charAt(2)).isEmpty() && !String.valueOf(s.charAt(3)).isEmpty())
                                    {
                                        matchOTPNumber(mobile, OTPNumber);
                                    }
                                    else
                                    {
                                        //FireToast.customSnackbar(OTP.this, "Enter OTP!!","");

                                        new SweetAlertDialog(OTP.this)
                                                .setTitleText("Enter OTP!!")
                                                .show();
                                    }
                                }
                            }

                        } // end for loop
                    } // bundle is null

                } catch (Exception e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);

                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(myReceiver);
            unregisterReceiver(networkUtilsReceiver);

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

    private void callVoiceOTP(final String MobileNo) {
        String url = Constants.VoiceOTP;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobile", MobileNo);

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OTP.this));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeader(OTP.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                VoiceOTPResponse(result);
            }
        });
    }

    private void VoiceOTPResponse(String Response) {
        try {


            sendOtpPOJO = gson.fromJson(Response, SendOtpPOJO.class);

            if (sendOtpPOJO.getMessage().equalsIgnoreCase("success")) {
                Timer();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void sendMobileNumber(final String MobileNo) {
        String url = Constants.SendOTP;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobileNo", MobileNo);
            postObject.put("id", SharedPrefUtil.getUser(OTP.this).getData().get_id());

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OTP.this));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeader(OTP.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                OTPResponse(result);
            }
        });
    }

    private void OTPResponse(String Response) {
        try {
            sendOtpPOJO = gson.fromJson(Response, SendOtpPOJO.class);

            if (sendOtpPOJO.getMessage().equalsIgnoreCase("Otp Send")) {
                Timer();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}