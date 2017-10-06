package com.kesari.trackingfresh.Payment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.kesari.trackingfresh.ConfirmOrder.OrderAddPojo;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.DashBoard.VerifyMobilePOJO;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.Default_DeliveryAddress;
import com.kesari.trackingfresh.Login.ProfileMain;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.OTP.OTP;
import com.kesari.trackingfresh.OTP.SendOtpPOJO;
import com.kesari.trackingfresh.OrderTracking.OrderBikerTrackingActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.ErrorPOJO;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.VehicleNearestRoute.NearestRouteMainPOJO;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class PaymentDetails extends AppCompatActivity implements PaymentResultListener,NetworkUtilsReceiver.NetworkResponseInt{

    FancyButton btnSubmit;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    private TextView price_payable,price_total;
    private RadioButton cash_on_delivery,online_payment;
    private RadioGroup payment_group;
    //private String OrderID = "";
    MyApplication myApplication;
    OrderAddPojo orderAddPojo;
    private Gson gson;
    boolean online = false,cod =false, wallet = false,walletOnly = false;
    VerifyMobilePOJO verifyMobilePOJO;
    SendOtpPOJO sendOtpPOJO;
    Dialog dialog;
    private ViewGroup mSnackbarContainer;
    //NearestVehicleMainPOJO nearestVehicleMainPOJO;
    CheckBox walletCash;
    ProfileMain profileMain;
    String walletAmount = "";
    int amountTotal,walletTotal;
    boolean TKFCash = false;

    EditText promocodeText;
    FancyButton promocodeSubmit;
    FancyButton cancel;

    ErrorPOJO errorPOJO;
    boolean clearDrawable = false;
    NearestRouteMainPOJO nearestRouteMainPOJO;

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
            gson = new Gson();

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

            price_total = (TextView) findViewById(R.id.price_total);
            price_payable = (TextView) findViewById(R.id.price_payable);
            cash_on_delivery = (RadioButton) findViewById(R.id.cash_on_delivery);
            online_payment = (RadioButton) findViewById(R.id.online_payment);
            walletCash = (CheckBox) findViewById(R.id.walletCash);
            btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);
            payment_group = (RadioGroup) findViewById(R.id.payment_group);

            promocodeSubmit = (FancyButton) findViewById(R.id.promocodeSubmit);
            promocodeText = (EditText) findViewById(R.id.promocodeText);
            cancel = (FancyButton) findViewById(R.id.cancel);

            promocodeText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(promocodeText.getText().toString().trim().isEmpty())
                    {
                        promocodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        clearDrawable = false;
                    }
                    else
                    {
                        promocodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                        clearDrawable = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            promocodeText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(clearDrawable)
                        {
                            if(event.getRawX() >= (promocodeText.getRight() - promocodeText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                // your action here
                                promocodeText.setText("");
                                price_payable.setText(getIntent().getStringExtra("amount"));
                                promocodeText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                clearDrawable = false;

                                promocodeSubmit.setEnabled(true);
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });

        cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promocodeText.setText("");
                    price_payable.setText(getIntent().getStringExtra("amount"));
                }
            });

            promocodeSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!promocodeText.getText().toString().isEmpty())
                    {
                        sendPromocode(promocodeText.getText().toString().trim(),price_payable.getText().toString().trim());
                    }
                    else
                    {
                        //Toast.makeText(PaymentDetails.this, "Enter Promocode!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(PaymentDetails.this)
                                .setTitleText("Enter Promocode!")
                                .show();
                    }
                }
            });

            try
            {

                price_total.setText(getIntent().getStringExtra("amount"));
                price_payable.setText(getIntent().getStringExtra("amount"));
                amountTotal = Integer.parseInt(getIntent().getStringExtra("amount"));

                walletCash.setText("Use cash from wallet" + " [ ₹ " + SharedPrefUtil.getUser(PaymentDetails.this).getData().getWalletAmount() + " ]");
                //OrderID = getIntent().getStringExtra("orderID");

            }catch (NullPointerException npe)
            {

            }

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btnSubmit.setClickable(false);

                    if(price_payable.getText().toString().equalsIgnoreCase("0"))
                    {
                        getVerifiedMobileNumber(SharedPrefUtil.getToken(PaymentDetails.this));
                        walletOnly = true;
                    }
                    else
                    {
                        if(online_payment.isChecked())
                        {
                            getVerifiedMobileNumber(SharedPrefUtil.getToken(PaymentDetails.this));
                            //addOrderListFromCart();
                            online = true;
                        }
                        else if(cash_on_delivery.isChecked())
                        {
                            getVerifiedMobileNumber(SharedPrefUtil.getToken(PaymentDetails.this));
                            //addOrderListFromCart();
                            cod = true;
                        }
                        else
                        {
                            //Toast.makeText(PaymentDetails.this, "Select Payment Mode!!", Toast.LENGTH_SHORT).show();
                            btnSubmit.setClickable(true);
                            new SweetAlertDialog(PaymentDetails.this)
                                    .setTitleText("Select Payment Mode!!")
                                    .show();
                        }
                    }
                /*Intent intent = new Intent(PaymentDetails.this, OrderReview.class);
                startActivity(intent);*/
                }
            });

            walletCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    promocodeText.setText("");

                    if(isChecked)
                    {
                        getProfileData();
                        TKFCash = true;
                    }
                    else
                    {
                        wallet = false;
                        TKFCash = false;

                        walletCash.setText("Use cash from wallet");
                        walletAmount = "";
                        price_payable.setText(getIntent().getStringExtra("amount"));
                        amountTotal = Integer.parseInt(getIntent().getStringExtra("amount"));
                        walletCash.setText("Use cash from wallet" + " [ ₹ " + SharedPrefUtil.getUser(PaymentDetails.this).getData().getWalletAmount() + " ]");

                        payment_group.setVisibility(View.VISIBLE);

                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void sendPromocode(final String Promocode, String Total)
    {
        try
        {

            String url = Constants.PromocodeValidity ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("total", Total);
                postObject.put("promoCode", Promocode.trim());

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("response", response.toString());
                            //dialog.dismiss();
                            PromocodeResponse(response.toString());
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //VolleyLog.d("Error", "Error: " + error.getMessage());
                    //dialog.dismiss();

                    try{
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        json = new String(response.data);
                        Log.d("Error", json);

                        ErrorResponse(json,PaymentDetails.this);

                    }catch (Exception e)
                    {
                        //Log.d("Error", e.getMessage());
                        //FireToast.customSnackbar(PaymentDetails.this, "Oops Something Went Wrong!!", "");

                        new SweetAlertDialog(PaymentDetails.this)
                                .setTitleText("Oops Something Went Wrong!!")
                                .show();
                    }
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                    return params;
                }
            };;

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

    private void ErrorResponse(String Response,Context context)
    {
        gson = new Gson();
        errorPOJO = gson.fromJson(Response, ErrorPOJO.class);

        if(errorPOJO.getErrors() != null)
        {
            String[] error = errorPOJO.getErrors();
            String errorString = error[0];

            //Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();

            new SweetAlertDialog(PaymentDetails.this)
                    .setTitleText(errorString)
                    .show();
        }
        else if(errorPOJO.getMessage() != null)
        {
            //Toast.makeText(context, errorPOJO.getMessage(), Toast.LENGTH_SHORT).show();

            new SweetAlertDialog(PaymentDetails.this)
                    .setTitleText(errorPOJO.getMessage())
                    .show();
        }
        else
        {
            //Toast.makeText(context, "Oops Something Went Wrong!!", Toast.LENGTH_SHORT).show();

            new SweetAlertDialog(PaymentDetails.this)
                    .setTitleText("Oops Something Went Wrong!!")
                    .show();
        }


    }

    private void PromocodeResponse(String response)
    {

        try
        {
            // {"newTotal":60}

            JSONObject jsonObject = new JSONObject(response);
            String newTotal = jsonObject.getString("newTotal");
            price_payable.setText(newTotal);

            promocodeSubmit.setEnabled(false);

        }catch (Exception e)
        {

        }
    }

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

            ioUtils.getPOSTStringRequestHeader(PaymentDetails.this,Constants.Profile, params, new IOUtils.VolleyCallback() {
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

            profileMain = gson.fromJson(Response,ProfileMain.class);

            if(profileMain.getData().getWalletAmount() != null)
            {
                if(!profileMain.getData().getWalletAmount().isEmpty() || !profileMain.getData().getWalletAmount().equalsIgnoreCase("0"))
                {
                    wallet = true;
                    if(amountTotal > Integer.parseInt(profileMain.getData().getWalletAmount()))
                    {
                        walletTotal = Integer.parseInt(profileMain.getData().getWalletAmount());
                        amountTotal = amountTotal - walletTotal;
                        price_payable.setText(String.valueOf(amountTotal));
                        walletCash.setText("Use cash from wallet" + " [ ₹ " + String.valueOf(0) + " ]");
                    }
                    else
                    {
                        walletTotal = Integer.parseInt(profileMain.getData().getWalletAmount());
                        walletTotal = walletTotal - amountTotal;
                        price_payable.setText(String.valueOf(0));
                        walletCash.setText("Use cash from wallet" + " [ ₹ " + String.valueOf(walletTotal) + " ]");
                    }

                    walletAmount = profileMain.getData().getWalletAmount();
                }
                else
                {
                    walletCash.setText("Use cash from wallet" + " [ ₹ 0 ]");
                    walletAmount = "0";
                    wallet = false;
                }

                if(price_payable.getText().toString().equalsIgnoreCase("0"))
                {
                    payment_group.setVisibility(View.GONE);
                }
            }
            else
            {
                walletAmount = "";
                wallet = false;
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getVerifiedMobileNumber(String Token)
    {
        try
        {

            String url = Constants.VerifyMobile + SharedPrefUtil.getUser(PaymentDetails.this).getData().get_id();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + Token);

            ioUtils.getGETStringRequestHeader(PaymentDetails.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    VerifyResponse(result);

                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnSubmit.setClickable(true);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void VerifyResponse(String Response)
    {
        try
        {

            verifyMobilePOJO = gson.fromJson(Response, VerifyMobilePOJO.class);

            if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile number not found"))
            {
                verifyMobileNumber("");
            }
            else if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile not Verified"))
            {
                verifyMobileNumber(verifyMobilePOJO.getMobileNo());
            }
            else if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile Verified"))
            {
                sendLATLONVehicle();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void sendLATLONVehicle()
    {
        try
        {

            String url = Constants.VehicleNearestRoute ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("longitude", SharedPrefUtil.getDefaultLocation(PaymentDetails.this).getLongitude());
                postObject.put("latitude", SharedPrefUtil.getDefaultLocation(PaymentDetails.this).getLatitude());

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(PaymentDetails.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    NearestVehicleResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnSubmit.setClickable(true);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void NearestVehicleResponse(String Response)
    {
        try
        {
            nearestRouteMainPOJO = gson.fromJson(Response, NearestRouteMainPOJO.class);

            if(nearestRouteMainPOJO.getData().isEmpty())
            {
                final Dialog dialog = new Dialog(PaymentDetails.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.item_unavailable_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                FancyButton btnCancel = (FancyButton) dialog.findViewById(R.id.btnCancel);
                btnCancel.setText("Sorry! We don't serve on this delivery address you have selected. Please add or change the delivery address!!");
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(PaymentDetails.this, Default_DeliveryAddress.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else
            {
                addOrderListFromCart();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void verifyMobileNumber(final String MobileNumber)
    {
        try {

            // Create custom dialog object
            dialog = new Dialog(PaymentDetails.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_verify_mobile);
            // Set dialog title
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
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

                    if(!mobile_number.isEmpty())
                    {
                        if (android.util.Patterns.PHONE.matcher(mobile_number).matches())
                        {
                            if (mobile_number.length() >= 10) {
                                sendMobileNumber(mobile.getText().toString(),mSnackbarContainer);
                            }
                            else
                            {
                                mobile.setError(getString(R.string.less_than_10digit));
                            }
                        }
                        else
                        {
                            mobile.setError(getString(R.string.proper_mobile));
                        }
                    }
                    else
                    {
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

        }catch (Exception e)
        {
            Log.i(TAG,"dialog_Mobile");
        }

    }

    private void sendMobileNumber(final String MobileNo, ViewGroup viewGroup)
    {
        String url = Constants.SendOTP ;

        Log.i("url", url);

        JSONObject jsonObject = new JSONObject();

        try {

            JSONObject postObject = new JSONObject();

            postObject.put("mobileNo", MobileNo);
            postObject.put("id",SharedPrefUtil.getUser(PaymentDetails.this).getData().get_id());

            jsonObject.put("post", postObject);

            Log.i("JSON CREATED", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

        IOUtils ioUtils = new IOUtils();

        ioUtils.sendJSONObjectRequestHeaderDialog(PaymentDetails.this, viewGroup, url, params ,jsonObject, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                OTPResponse(result,MobileNo);
            }
        }, new IOUtils.VolleyFailureCallback() {
            @Override
            public void onFailure(String result) {

            }
        });
    }

    private void OTPResponse(String Response,String mobile)
    {
        try
        {
            sendOtpPOJO = gson.fromJson(Response, SendOtpPOJO.class);

            if(sendOtpPOJO.getMessage().equalsIgnoreCase("Otp Send"))
            {
                Intent intent = new Intent(PaymentDetails.this, OTP.class);
                intent.putExtra("mobile_num",mobile);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void addOrderListFromCart()
    {
        try
        {

            String url = Constants.AddOrder ;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                //JSONArray postObject = new JSONArray();

                JSONObject postObject = new JSONObject();
                JSONArray cartItemsArray = new JSONArray();
                JSONObject cartItemsObjedct;
                for (int i = 0; i < myApplication.getProductsArraylist().size(); i++)
                {
                    cartItemsObjedct = new JSONObject();
                    cartItemsObjedct.put("productId", myApplication.getProductsArraylist().get(i).getProductId());
                    cartItemsObjedct.put("productName",myApplication.getProductsArraylist().get(i).getProductName());
                    cartItemsObjedct.put("quantity",myApplication.getProductsArraylist().get(i).getQuantity());
                    cartItemsObjedct.put("price",myApplication.getProductsArraylist().get(i).getPrice());
                    cartItemsObjedct.put("active",myApplication.getProductsArraylist().get(i).getActive());

                    //Below Items required for repeat orders
                    cartItemsObjedct.put("productCategory",myApplication.getProductsArraylist().get(i).getProductCategory());
                    cartItemsObjedct.put("_id",myApplication.getProductsArraylist().get(i).get_id());
                    cartItemsObjedct.put("unitsOfMeasurement",myApplication.getProductsArraylist().get(i).getUnitsOfMeasurement());
                    cartItemsObjedct.put("productCategoryId",myApplication.getProductsArraylist().get(i).getProductCategoryId());
                    cartItemsObjedct.put("productDescription",myApplication.getProductsArraylist().get(i).getProductDescription());
                    cartItemsObjedct.put("productDetails",myApplication.getProductsArraylist().get(i).getProductDetails());
                    cartItemsObjedct.put("unit",myApplication.getProductsArraylist().get(i).getUnit());
                    cartItemsObjedct.put("unitsOfMeasurementId",myApplication.getProductsArraylist().get(i).getUnitsOfMeasurementId());
                    cartItemsObjedct.put("productImage",myApplication.getProductsArraylist().get(i).getProductImage());
                    cartItemsObjedct.put("brand",myApplication.getProductsArraylist().get(i).getBrand());
                    cartItemsObjedct.put("availableQuantity",myApplication.getProductsArraylist().get(i).getAvailableQuantity());
                    cartItemsObjedct.put("MRP",myApplication.getProductsArraylist().get(i).getMRP());

                    cartItemsArray.put(cartItemsObjedct);
                }

                postObject.put("orders",cartItemsArray);
                postObject.put("total_price",getIntent().getStringExtra("amount"));
                postObject.put("pickUp",getIntent().getBooleanExtra("isPickup",false));

                if(TKFCash)
                {
                    postObject.put("walletAmount",walletAmount);
                }

                postObject.put("vehicleId",SharedPrefUtil.getNearestRouteMainPOJO(PaymentDetails.this).getData().get(0).getVehicleId());
                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(PaymentDetails.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(PaymentDetails.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    OrderSendResponse(result);
                    btnSubmit.setClickable(true);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnSubmit.setClickable(true);
                }
            });
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void OrderSendResponse(String Response)
    {
        try
        {
            orderAddPojo = gson.fromJson(Response, OrderAddPojo.class);

            if(!orderAddPojo.getMessage().get_id().isEmpty())
            {

                if(wallet)
                {

                    if(walletOnly)
                    {
                        Toast.makeText(PaymentDetails.this, "Wallet Payment!!", Toast.LENGTH_SHORT).show();
                        updateOrderDetails(orderAddPojo.getMessage().get_id(),"Wallet","");
                    }
                    else if(online)
                    {
                        startPayment(orderAddPojo.getMessage().get_id(),getIntent().getStringExtra("amount"));
                        Toast.makeText(PaymentDetails.this, "Online Payment & Wallet", Toast.LENGTH_SHORT).show();
                    }
                    else if(cod)
                    {
                        Toast.makeText(PaymentDetails.this, "Cash On Delivery & Wallet!!", Toast.LENGTH_SHORT).show();
                        updateOrderDetails(orderAddPojo.getMessage().get_id(),"COD,Wallet","");
                    }

                }
                else
                {
                    if(online)
                    {
                        startPayment(orderAddPojo.getMessage().get_id(),getIntent().getStringExtra("amount"));
                        Toast.makeText(PaymentDetails.this, "Online Payment", Toast.LENGTH_SHORT).show();
                    }

                    if(cod)
                    {
                        Toast.makeText(PaymentDetails.this, "Cash On Delivery!!", Toast.LENGTH_SHORT).show();
                        updateOrderDetails(orderAddPojo.getMessage().get_id(),"COD","");
                    }
                }


            }
            else
            {
                //FireToast.customSnackbar(PaymentDetails.this, "Order Failed!!", "");

                new SweetAlertDialog(PaymentDetails.this)
                        .setTitleText("Order Failed!!")
                        .show();
            }

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
             * Eg: "500" = ₹ 5.00
             */
            int orderWallet = Integer.parseInt(price_payable.getText().toString()) * 100;
            options.put("amount", String.valueOf(orderWallet));

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

        if(wallet)
        {
            updateOrderDetails(orderAddPojo.getMessage().get_id(),"Online Payment,Wallet",razorpayPaymentID);
        }
        else
        {
            updateOrderDetails(orderAddPojo.getMessage().get_id(),"Online Payment",razorpayPaymentID);
        }

    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */

        //updateOrderDetails(OrderID,"Online Payment","SampLe123");

        Log.i("Payment","Error");
        Log.i("Payment",response);
        CancelOrder(orderAddPojo.getMessage().get_id(),"Cancelled");
    }

    private void CancelOrder(String orderID, String OrderStatus) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);
                postObject.put("cancelReason","Payment Failure");

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

                    CancelResponse(result);
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

    private void CancelResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                Log.i(TAG, Response);
                Intent intent = new Intent(PaymentDetails.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                myApplication.removeProductsItems();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void updateOrderDetails(String orderID, String payment_mode,String paymentId) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status","Pending");
                postObject.put("payment_Mode",payment_mode);

                if(payment_mode.equalsIgnoreCase("Wallet"))
                {
                    postObject.put("payment_Status","Received");
                }
                else if(payment_mode.equalsIgnoreCase("Online Payment"))
                {
                    postObject.put("payment_Status","Received");
                    postObject.put("payment_Id",paymentId);
                }
                else if(payment_mode.equalsIgnoreCase("Online Payment,Wallet"))
                {
                    postObject.put("payment_Status","Received");
                    postObject.put("payment_Id",paymentId);
                }
                else if(payment_mode.equalsIgnoreCase("COD,Wallet"))
                {
                    postObject.put("payment_Status","Pending");
                }
                else if(payment_mode.equalsIgnoreCase("COD"))
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
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

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
                intent.putExtra("orderID",orderAddPojo.getMessage().get_id());
                startActivity(intent);
                finish();
                myApplication.removeProductsItems();
            }
            else
            {
                //Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(PaymentDetails.this)
                        .setTitleText("Payment Failed")
                        .show();
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
