package com.kesari.trackingfresh.Payment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.OrderTracking.OrderBikerTrackingActivity;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentDetails extends AppCompatActivity implements PaymentResultListener,NetworkUtilsReceiver.NetworkResponseInt{

    Button btnSubmit;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    private TextView price_payable;
    private RadioButton cash_on_delivery,online_payment;
    private String OrderID = "";
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            myApplication = (MyApplication) getApplicationContext();

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(PaymentDetails.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            /**
             * Preload payment resources
             */
            Checkout.preload(getApplicationContext());

            price_payable = (TextView) findViewById(R.id.price_payable);
            cash_on_delivery = (RadioButton) findViewById(R.id.cash_on_delivery);
            online_payment = (RadioButton) findViewById(R.id.online_payment);

            btnSubmit = (Button) findViewById(R.id.btnSubmit);

            try
            {

                price_payable.setText(getIntent().getStringExtra("amount"));
                OrderID = getIntent().getStringExtra("orderID");

            }catch (NullPointerException npe)
            {

            }

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(online_payment.isChecked())
                    {
                        startPayment(OrderID,"100");
                        Toast.makeText(PaymentDetails.this, "Online Payment", Toast.LENGTH_SHORT).show();
                    }
                    else if(cash_on_delivery.isChecked())
                    {
                        Toast.makeText(PaymentDetails.this, "Cash On Delivery!!", Toast.LENGTH_SHORT).show();

                        updateOrderDetails(OrderID,"COD","");
                    }
                    else
                    {
                        Toast.makeText(PaymentDetails.this, "Select Payment Mode!!", Toast.LENGTH_SHORT).show();
                    }
                /*Intent intent = new Intent(PaymentDetails.this, OrderReview.class);
                startActivity(intent);*/
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void startPayment(String orderID,String priceTotal) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Tracking Fresh");

            /**
             * Description can be anything
             * eg: Order #123123
             *     Invoice Payment
             *     etc.
             */
            options.put("description", orderID);

            options.put("currency", "INR");

            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            options.put("amount", "100");

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successfull payment response
         */

        Log.i("Payment","Success");
        Log.i("PaymentID",razorpayPaymentID);

        updateOrderDetails(OrderID,"Online Payment",razorpayPaymentID);
    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */

        updateOrderDetails(OrderID,"Online Payment","SampLe123");

        Log.i("Payment","Error");
        Log.i("Payment",response);
    }


    private void updateOrderDetails(String orderID, String payment_mode,String paymentId) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status","Accepted");
                postObject.put("payment_Mode",payment_mode);

                if(!payment_mode.equalsIgnoreCase("COD"))
                {
                    postObject.put("payment_Status","Received");
                    postObject.put("payment_Id",paymentId);
                }
                else
                {
                    postObject.put("payment_Status","Pending");
                }

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

            ioUtils.sendJSONObjectPutRequestHeader(PaymentDetails.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    PaymentUpdateResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void PaymentUpdateResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                Intent intent = new Intent(PaymentDetails.this, OrderBikerTrackingActivity.class);
                intent.putExtra("orderID",OrderID);
                startActivity(intent);
                finish();

                myApplication.removeProductsItems();
            }
            else
            {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
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

}
