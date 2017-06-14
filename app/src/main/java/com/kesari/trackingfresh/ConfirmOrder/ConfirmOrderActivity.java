package com.kesari.trackingfresh.ConfirmOrder;

import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.Payment.PaymentDetails;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.network.FireToast;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONObject;

public class ConfirmOrderActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    TextView order_placed_by,order_status,order_total;
    RecyclerView recyclerViewConfirmOrder;
    private LinearLayoutManager confirmOrderLayoutManager;
    private Gson gson;
    private RecyclerView.Adapter confirmOrderAdapter;
    private OrderAddPojo orderAddPojo;
    private Button confirm_Order_pay;

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
            confirm_Order_pay = (Button) findViewById(R.id.btnSubmit);
            recyclerViewConfirmOrder = (RecyclerView) findViewById(R.id.recyclerView);

            recyclerViewConfirmOrder.setHasFixedSize(true);
            confirmOrderLayoutManager = new LinearLayoutManager(ConfirmOrderActivity.this);
            confirmOrderLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewConfirmOrder.setLayoutManager(confirmOrderLayoutManager);

            confirm_Order_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!orderAddPojo.getMessage().get_id().isEmpty())
                    {
                        Intent intent = new Intent(ConfirmOrderActivity.this, PaymentDetails.class);
                        intent.putExtra("orderID",orderAddPojo.getMessage().get_id());
                        intent.putExtra("amount",orderAddPojo.getMessage().getTotal_price());
                        startActivity(intent);
                        finish();
                    }
                }
            });

            try
            {
                OrderSendResponse(getIntent().getStringExtra("confirmOrder"));
                Log.i(TAG,getIntent().getStringExtra("confirmOrder"));
            }catch (NullPointerException npe)
            {
                Log.i("EXCEPTIOM","Exception");
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void OrderSendResponse(String Response)
    {
        try
        {
            orderAddPojo = gson.fromJson(Response, OrderAddPojo.class);

            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(!orderAddPojo.getMessage().get_id().isEmpty())
            {
                order_placed_by.setText(orderAddPojo.getMessage().getCreatedBy());
                order_status.setText(orderAddPojo.getMessage().getStatus());
                order_total.setText(orderAddPojo.getMessage().getTotal_price());

                confirmOrderAdapter = new ConfirmOrder_RecyclerAdpater(orderAddPojo.getMessage().getOrder(),ConfirmOrderActivity.this);
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
}
