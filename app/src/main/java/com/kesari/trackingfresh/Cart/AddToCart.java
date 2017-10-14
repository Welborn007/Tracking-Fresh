package com.kesari.trackingfresh.Cart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.DashBoard.DashboardActivity;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.Default_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.FetchAddressPOJO;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class AddToCart extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private NetworkUtilsReceiver networkUtilsReceiver;
    GridView gridview;
    private MyDataAdapter myDataAdapter;
    //List<Product_POJO> product_pojos = new ArrayList<>();
    TextView cart_count;
    FancyButton checkOut;
    MyApplication myApplication;
    private RelativeLayout relativeLayout;
    private TextView valueTV;
    private String TAG = this.getClass().getSimpleName();
    private Gson gson;
    private FetchAddressPOJO fetchAddressPOJO;

    //ScheduledExecutorService scheduleTaskExecutor;
    public static int mNotificationsCount = 0;
    int Total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.porcelain));

            myApplication = (MyApplication) getApplicationContext();

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                IOUtils.buildAlertMessageNoGps(AddToCart.this);
            } else {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            gridview = (GridView) findViewById(R.id.list);
            cart_count = (TextView) findViewById(R.id.cart_count);
            checkOut = (FancyButton) findViewById(R.id.checkOut);
            relativeLayout = (RelativeLayout) findViewById(R.id.relativelay_reclview);
            valueTV = new TextView(AddToCart.this);

            if(getIntent().getStringExtra("productRemoved") != null)
            {
                if(getIntent().getStringExtra("productRemoved").equalsIgnoreCase("true"))
                {
                    new SweetAlertDialog(AddToCart.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Some products were not added as Out of Stock!")
                            .show();
                }
            }

            if(myApplication.getProductsArraylist() != null)
            {
                if (myApplication.getProductsArraylist().isEmpty()) {
                    gridview.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    valueTV.setText("No Items available in Cart");
                    valueTV.setGravity(Gravity.CENTER);
                    valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    ((RelativeLayout) relativeLayout).addView(valueTV);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    gridview.setVisibility(View.VISIBLE);

                    getProductData();
                }
            }
            else
            {
                gridview.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                valueTV.setText("No Items available in Cart");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);
            }

            checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try
                    {
                        if (!myApplication.getProductsArraylist().isEmpty()) {
                            fetchUserAddress();
                        } else {
                            //Toast.makeText(AddToCart.this, "No Items in Cart!!", Toast.LENGTH_SHORT).show();

                            new SweetAlertDialog(AddToCart.this)
                                    .setTitleText("No Items in Cart!!")
                                    .show();
                        }
                    }catch (Exception e)
                    {
                        //Toast.makeText(AddToCart.this, "No Items in Cart!!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(AddToCart.this)
                                .setTitleText("No Items in Cart!!")
                                .show();
                    }

                }
            });

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

    private void fetchUserAddress() {
        try {

            String url = Constants.FetchAddress;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(AddToCart.this));

            ioUtils.getGETStringRequestHeader(AddToCart.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    fetchUserAddressResponse(result);
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

    private void fetchUserAddressResponse(String Response) {
        try {

            fetchAddressPOJO = gson.fromJson(Response, FetchAddressPOJO.class);

            if (fetchAddressPOJO.getData().isEmpty()) {
                Intent intent = new Intent(AddToCart.this, Add_DeliveryAddress.class);
                startActivity(intent);
                finish();
            } else {

                Intent intent = new Intent(AddToCart.this, Default_DeliveryAddress.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getProductData() {
        try {

            myDataAdapter = new MyDataAdapter(myApplication.getProductsArraylist(), AddToCart.this);
            gridview.setAdapter(myDataAdapter);
            myDataAdapter.notifyDataSetChanged();

            /*for (int i = 0; i < myApplication.getProductsArraylist().size(); i++)
            {
                Total = Integer.parseInt(myApplication.getProductsArraylist().get(i).getPrice()) * myApplication.getProductsArraylist().get(i).getQuantity();
            }
*/
            cart_count.setText(String.valueOf(myApplication.getProductsArraylist().size()) + " Products" /*+ String.valueOf(Total)*/);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyDataAdapter<T> extends BaseAdapter {
        List<AddCart_model> AddCart_models;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<AddCart_model> AddCart_models, Activity activity) {
            this.AddCart_models = AddCart_models;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return AddCart_models.size();
        }

        @Override
        public Object getItem(int position) {
            return AddCart_models.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final MyDataAdapter.ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new MyDataAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.card_layout, null);

                viewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

                viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.price);

                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (ImageView) convertView.findViewById(R.id.plus);
                viewHolder.minus = (ImageView) convertView.findViewById(R.id.minus);
                viewHolder.delete = (FancyButton) convertView.findViewById(R.id.delete);

                viewHolder.quantity_price = (TextView) convertView.findViewById(R.id.quantity_price);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyDataAdapter.ViewHolder) convertView.getTag();
            }

            try {

                final AddCart_model product_pojo = AddCart_models.get(position);

                viewHolder.product_name.setText(product_pojo.getProductName());

                viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity, product_pojo.getProductImage()));
                viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));

                viewHolder.weight.setText(product_pojo.getUnit() + product_pojo.getUnitsOfMeasurement());
                viewHolder.price.setText("₹ " + product_pojo.getPrice());

                viewHolder.count.setText(String.valueOf(product_pojo.getQuantity()));
                viewHolder.quantity_price.setText("₹ " + String.valueOf(Integer.parseInt(product_pojo.getPrice()) * product_pojo.getQuantity()));

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myApplication.removeProducts(position);
                        notifyDataSetChanged();
                        getProductData();

                        //Total = Total - Integer.parseInt(viewHolder.quantity_price.getText().toString());
                        //cart_count.setText(String.valueOf(myApplication.getProductsArraylist().size()) + " Products - Rs. " + String.valueOf(Total));
                    }
                });

                viewHolder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int t = Integer.parseInt(viewHolder.count.getText().toString());
                            //viewHolder.count.setText(String.valueOf(t + 1));

                            //DashboardActivity.updateNotificationsBadge(t + 1);


                            if (t < Integer.parseInt(product_pojo.getAvailableQuantity()))
                            {
                                viewHolder.count.setText(String.valueOf(t + 1));
                                viewHolder.quantity_price.setText(String.valueOf(Integer.parseInt(product_pojo.getPrice()) * (t + 1)));

                                //Total = Total + Integer.parseInt(product_pojo.getPrice());
                                //cart_count.setText(String.valueOf(myApplication.getProductsArraylist().size()) + " Products - Rs. " + String.valueOf(Total));

                                if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }

                            } else {
                                final Dialog dialog = new Dialog(activity);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_unavailable_dialog);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.show();

                                FancyButton btnCancel = (FancyButton) dialog.findViewById(R.id.btnCancel);
                                btnCancel.setText("Oops! Only " + product_pojo.getAvailableQuantity() + " items available!!");
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }

                            /*if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                            } else {

                            }
*/
                        } catch (Exception e) {

                        }

                    }
                });

                viewHolder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int t = Integer.parseInt(viewHolder.count.getText().toString());
                            if (t > 0) {
                                viewHolder.count.setText(String.valueOf(t - 1));

                                //DashboardActivity.updateNotificationsBadge(t - 1);
                                viewHolder.quantity_price.setText(String.valueOf(Integer.parseInt(product_pojo.getPrice()) * (t - 1)));

                                //Total = Total - Integer.parseInt(product_pojo.getPrice());
                                //cart_count.setText(String.valueOf(myApplication.getProductsArraylist().size()) + " Products - Rs. " + String.valueOf(Total));

                                if (!myApplication.DecrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            }

                            if (t == 1) {

                                myApplication.RemoveProductonZeroQuantity(product_pojo.getProductId());
                                //myApplication.removeProducts(position);
                                notifyDataSetChanged();
                                getProductData();
                                viewHolder.count.setText("0");
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            /*viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(activity, DetailsActivity.class);
                    startActivity(in);
                }
            });*/

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return convertView;
        }

        private class ViewHolder {
            TextView product_name, weight, price, count,quantity_price;
            SimpleDraweeView imageView;
            ImageView plus, minus;
            FancyButton delete;
        }
    }

    public String loadProductJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("products_mock.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tocart, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        setBadgeCount(this, icon, mNotificationsCount);
//        BitmapDrawable iconBitmap = (BitmapDrawable) item.getIcon();
//        LayerDrawable iconLayer = new LayerDrawable(new Drawable[] { iconBitmap });
//        setBadgeCount(this, iconLayer, mNotificationsCount);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        invalidateOptionsMenu();
        return true;
    }

    public static void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddToCart.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddToCart.this, DashboardActivity.class);
        startActivity(intent);
        finish();
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

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
