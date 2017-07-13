package com.kesari.trackingfresh.Order;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.Cart.AddToCart;
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

import java.util.HashMap;
import java.util.Map;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class OrderReview extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    String OrderID = "";
    private RecyclerView recListProducts;
    private LinearLayoutManager ProductsLayout;
    private Gson gson;
    OrderReviewMainPOJO orderReviewMainPOJO;
    private RecyclerView.Adapter adapterProducts;
    TextView total_price,payment_status,payment_mode,fullName,buildingName,landmark,address,mobileNo;
    Button btnSubmit;

    //ScheduledExecutorService scheduleTaskExecutor;
    MyApplication myApplication;
    public static int mNotificationsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            OrderID = getIntent().getStringExtra("orderID");
            recListProducts = (RecyclerView) findViewById(R.id.recyclerView);

            recListProducts.setHasFixedSize(true);
            ProductsLayout = new LinearLayoutManager(OrderReview.this);
            ProductsLayout.setOrientation(LinearLayoutManager.VERTICAL);
            recListProducts.setLayoutManager(ProductsLayout);

            total_price = (TextView) findViewById(R.id.total_price);
            payment_status = (TextView) findViewById(R.id.payment_status);
            payment_mode = (TextView) findViewById(R.id.payment_mode);
            fullName = (TextView) findViewById(R.id.fullName);
            buildingName = (TextView) findViewById(R.id.buildingName);
            landmark = (TextView) findViewById(R.id.landmark);
            address = (TextView) findViewById(R.id.address);
            mobileNo = (TextView) findViewById(R.id.mobileNo);

            btnSubmit = (Button) findViewById(R.id.btnSubmit);

            final String orderID = getIntent().getStringExtra("orderID");

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderReview.this, OrderBikerTrackingActivity.class);
                    intent.putExtra("orderID",orderID);
                    startActivity(intent);
                    finish();
                }
            });

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(OrderReview.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            getOrderDetailsfromID();


            myApplication = (MyApplication) getApplicationContext();

            updateNotificationsBadge(myApplication.getProductsArraylist().size());

            /*scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

            // This schedule a task to run every 10 minutes:
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    updateNotificationsBadge(myApplication.getProductsArraylist().size());
                }
            }, 0, 1, TimeUnit.SECONDS);*/

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void getOrderDetailsfromID() {
        try {

            String url = Constants.OrderDetails + OrderID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OrderReview.this));

            ioUtils.getGETStringRequestHeader(OrderReview.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    OrderDetailsResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void OrderDetailsResponse(String Response)
    {
        try
        {
            orderReviewMainPOJO = gson.fromJson(Response, OrderReviewMainPOJO.class);

            if(orderReviewMainPOJO.getData().getStatus().equalsIgnoreCase("Pending") || orderReviewMainPOJO.getData().getStatus().equalsIgnoreCase("Accepted"))
            {
                btnSubmit.setVisibility(View.VISIBLE);
            }
            else
            {
                btnSubmit.setVisibility(View.GONE);
            }

            adapterProducts = new OrderReViewRecyclerAdapter(orderReviewMainPOJO.getData().getOrder(),OrderReview.this);
            recListProducts.setAdapter(adapterProducts);

            payment_status = (TextView) findViewById(R.id.payment_status);
            payment_mode = (TextView) findViewById(R.id.payment_mode);
            fullName = (TextView) findViewById(R.id.fullName);
            buildingName = (TextView) findViewById(R.id.buildingName);
            landmark = (TextView) findViewById(R.id.landmark);
            address = (TextView) findViewById(R.id.address);
            mobileNo = (TextView) findViewById(R.id.mobileNo);

            total_price.setText(orderReviewMainPOJO.getData().getTotal_price() + " .Rs");

            if(orderReviewMainPOJO.getData().getPayment_Status() != null)
            {
                payment_status.setText(orderReviewMainPOJO.getData().getPayment_Status());
            }

            if(orderReviewMainPOJO.getData().getPayment_Mode() != null)
            {
                payment_mode.setText(orderReviewMainPOJO.getData().getPayment_Mode());
            }

            fullName.setText(orderReviewMainPOJO.getData().getAddress().getFullName());
            buildingName.setText(orderReviewMainPOJO.getData().getAddress().getFlat_No() + ", " + orderReviewMainPOJO.getData().getAddress().getBuildingName());
            landmark.setText(orderReviewMainPOJO.getData().getAddress().getLandmark());
            address.setText(orderReviewMainPOJO.getData().getAddress().getCity() + ", " + orderReviewMainPOJO.getData().getAddress().getState() + ", " + orderReviewMainPOJO.getData().getAddress().getPincode());
            mobileNo.setText(orderReviewMainPOJO.getData().getAddress().getMobileNo());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tocart, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        setBadgeCount(this, icon, mNotificationsCount);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hot:
                Intent intent = new Intent(OrderReview.this, AddToCart.class);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
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
}
