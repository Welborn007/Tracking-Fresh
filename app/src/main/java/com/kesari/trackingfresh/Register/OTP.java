package com.kesari.trackingfresh.Register;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private NetworkUtilsReceiver networkUtilsReceiver;
    Button send, skip, resend;
    EditText Otp1,Otp2,Otp3,Otp4;

    Dialog dialog;

    TextView counter,number;
    String mobile,username;

    IntentFilter filter1;
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public static final String SMS_RECEIVED_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String SMS_RECEIVED_ACTION1 = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otp);

        /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        send = (Button) findViewById(R.id.sendOTP);
        skip = (Button) findViewById(R.id.skip);

        Otp1 = (EditText) findViewById(R.id.otp1);
        Otp2 = (EditText) findViewById(R.id.otp2);
        Otp3 = (EditText) findViewById(R.id.otp3);
        Otp4 = (EditText) findViewById(R.id.otp4);

        resend = (Button) findViewById(R.id.resendOTP);
        counter = (TextView) findViewById(R.id.counter);
        number = (TextView) findViewById(R.id.mobinumber);

        Timer();

        resend.setEnabled(false);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timer();
                resend.setEnabled(false);
            }
        });

        filter1 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(myReceiver, filter1);
    }

    public void Timer()
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
                resend.setEnabled(true);
            }
        }.start();
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
                                }
                            }

                        } // end for loop
                    } // bundle is null

                } catch (NullPointerException e) {
                    Log.e("SmsReceiver", "Exception smsReceiver" +e);

                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(myReceiver);
        unregisterReceiver(networkUtilsReceiver);
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
}