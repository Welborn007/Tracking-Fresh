package com.kesari.trackingfresh.ConfirmOrder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.DeliveryAddress.OrderFareMainPOJO;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.Payment.PaymentDetails;
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

public class ConfirmOrderActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    TextView order_placed_by,order_status,order_total,delivery_charge;
    RecyclerView recyclerViewConfirmOrder;
    private LinearLayoutManager confirmOrderLayoutManager;
    private Gson gson;
    private RecyclerView.Adapter confirmOrderAdapter;
    private OrderFareMainPOJO orderFareMainPOJO;
    private FancyButton confirm_Order_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

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
                IOUtils.buildAlertMessageNoGps(ConfirmOrderActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            order_placed_by = (TextView) findViewById(R.id.order_placed_by);
            order_status = (TextView) findViewById(R.id.order_status);
            order_total = (TextView) findViewById(R.id.order_total);
            delivery_charge = (TextView) findViewById(R.id.delivery_charge);
            confirm_Order_pay = (FancyButton) findViewById(R.id.btnSubmit);
            recyclerViewConfirmOrder = (RecyclerView) findViewById(R.id.recyclerView);

            order_placed_by.setText(getIntent().getStringExtra("OrderPlacedBy"));

            recyclerViewConfirmOrder.setHasFixedSize(true);
            confirmOrderLayoutManager = new LinearLayoutManager(ConfirmOrderActivity.this);
            confirmOrderLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewConfirmOrder.setLayoutManager(confirmOrderLayoutManager);

            try
            {
                OrderSendResponse(getIntent().getStringExtra("confirmOrder"));
                Log.i(TAG,getIntent().getStringExtra("confirmOrder"));
            }catch (NullPointerException npe)
            {
                Log.i("EXCEPTIOM","Exception");
            }

            confirm_Order_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!orderFareMainPOJO.getData().getOrders().isEmpty())
                    {
                        Intent intent = new Intent(ConfirmOrderActivity.this, PaymentDetails.class);
                        //intent.putExtra("orderID",orderAddPojo.getMessage().get_id());
                        intent.putExtra("isPickup",getIntent().getBooleanExtra("isPickup",false));
                        intent.putExtra("amount",orderFareMainPOJO.getData().getTotal_price());
                        startActivity(intent);
                        finish();
                    }
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
            orderFareMainPOJO = gson.fromJson(Response, OrderFareMainPOJO.class);

            JSONObject jsonObject = new JSONObject(Response);

            //String Message = jsonObject.getString("message");

            if(!orderFareMainPOJO.getData().getOrders().isEmpty())
            {
                //order_placed_by.setText(orderAddPojo.getMessage().getCreatedBy());
                //order_status.setText(orderAddPojo.getMessage().getStatus());
                order_total.setText("₹ " + orderFareMainPOJO.getData().getTotal_price());
                delivery_charge.setText("₹ " + orderFareMainPOJO.getData().getDelivery_charge());

                confirmOrderAdapter = new ConfirmOrder_RecyclerAdpater(orderFareMainPOJO.getData().getOrders(),ConfirmOrderActivity.this);
                recyclerViewConfirmOrder.setAdapter(confirmOrderAdapter);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
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

    private void updateImageDialog()
    {

        final CharSequence[] options = { "Yes","Cancel" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);

        builder.setTitle("Are You Sure You Want to Cancel the Order?");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Yes"))
                {
                    dialog.cancel();
                    //updateOrderDetails(orderAddPojo.getMessage().get_id(),"Cancelled");
                }
                else if (options[item].equals("Cancel")) {

                    dialog.cancel();
                }

            }

        });

        builder.show();
    }

    private void updateOrderDetails(String orderID, String OrderStatus) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ConfirmOrderActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(ConfirmOrderActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
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
                Log.i(TAG, Response);
                Intent intent = new Intent(ConfirmOrderActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //updateImageDialog();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {


        updateImageDialog();
    }*/
}
