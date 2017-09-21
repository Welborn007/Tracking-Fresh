package com.kesari.trackingfresh.MyOffers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.DetailPage.DetailsActivity;
import com.kesari.trackingfresh.Map.LocationServiceNew;
import com.kesari.trackingfresh.ProductSubFragment.SubProductMainPOJO;
import com.kesari.trackingfresh.ProductSubFragment.SubProductSubPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.MyApplication;
import com.kesari.trackingfresh.network.NetworkUtils;
import com.kesari.trackingfresh.network.NetworkUtilsReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class MyOffersActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    public RecyclerView.Adapter adapterOrders;
    public RecyclerView recListOffers;
    public LinearLayoutManager Orders;
    public Gson gson;
    MyApplication myApplication;
    public static int mNotificationsCount = 0;

    private  SwipeRefreshLayout swipeContainer;
    private SubProductMainPOJO subProductMainPOJO;
    public  RelativeLayout relativeLayout;
    public  TextView valueTV;

    GridView gridview;
    private MyDataAdapter myDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getBackground().setAlpha(0);

            gson = new Gson();

            recListOffers = (RecyclerView) findViewById(R.id.recyclerView);

            recListOffers.setHasFixedSize(true);
            Orders = new LinearLayoutManager(MyOffersActivity.this);
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOffers.setLayoutManager(Orders);

            relativeLayout = (RelativeLayout) findViewById(R.id.relativelay_reclview);
            gridview = (GridView) findViewById(R.id.list);

            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getOffersList(MyOffersActivity.this);

                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);


            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(MyOffersActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            getOffersList(MyOffersActivity.this);

            myApplication = (MyApplication) getApplicationContext();
            if(myApplication.getProductsArraylist() != null)
            {
                updateNotificationsBadge(myApplication.getProductsArraylist().size());
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getOffersList(final Context context)
    {
        try
        {

            String url = Constants.ProductOffers + SharedPrefUtil.getNearestRouteMainPOJO(MyOffersActivity.this).getData().get(0).getVehicleId();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("OffersList", result.toString());

                    swipeContainer.setRefreshing(false);

                    getProductDataResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i("OffersList", e.getMessage());
        }
    }

    public void getProductDataResponse(String Response) {

        try {

            subProductMainPOJO = gson.fromJson(Response, SubProductMainPOJO.class);
            valueTV = new TextView(MyOffersActivity.this);

            if(subProductMainPOJO.getData().isEmpty())
            {
                myDataAdapter = new MyDataAdapter(subProductMainPOJO.getData(), MyOffersActivity.this);
                gridview.setAdapter(myDataAdapter);
                myDataAdapter.notifyDataSetChanged();

                gridview.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Offers Found!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);

            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                gridview.setVisibility(View.VISIBLE);

                myDataAdapter = new MyDataAdapter(subProductMainPOJO.getData(), MyOffersActivity.this);
                gridview.setAdapter(myDataAdapter);
                myDataAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                Intent intent = new Intent(MyOffersActivity.this, AddToCart.class);
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

    class MyDataAdapter<T> extends BaseAdapter {
        List<SubProductSubPOJO> SubProductSubPOJOs;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<SubProductSubPOJO> SubProductSubPOJOs, Activity activity) {
            this.SubProductSubPOJOs = SubProductSubPOJOs;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return SubProductSubPOJOs.size();
        }

        @Override
        public Object getItem(int position) {
            return SubProductSubPOJOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyDataAdapter.ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new MyDataAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.product_layout, null);

                viewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

                viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.price);
                viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (Button) convertView.findViewById(R.id.plus);
                viewHolder.minus = (Button) convertView.findViewById(R.id.minus);
                viewHolder.mrp = (TextView) convertView.findViewById(R.id.mrp);
                viewHolder.addtoCart = (FancyButton) convertView.findViewById(R.id.addtoCart);
                viewHolder.holder_count = (LinearLayout) convertView.findViewById(R.id.holder_count);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyDataAdapter.ViewHolder) convertView.getTag();
            }

            try {

                final SubProductSubPOJO product_pojo = SubProductSubPOJOs.get(position);

                viewHolder.product_name.setText(product_pojo.getProductName());

                viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity, product_pojo.getProductImage()));
                viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));
                viewHolder.quantity.setText(product_pojo.getAvailableQuantity() + " quantity");
                viewHolder.weight.setText(product_pojo.getUnit() + product_pojo.getUnitsOfMeasurement());
                viewHolder.price.setText("₹ " + product_pojo.getSelling_price());

                viewHolder.mrp.setPaintFlags(viewHolder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mrp.setText("₹ " + product_pojo.getMRP());

                viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.holder_count.setVisibility(View.VISIBLE);
                        viewHolder.addtoCart.setVisibility(View.GONE);
                        viewHolder.count.setText("1");


                        if (!myApplication.checkifproductexists(product_pojo.getProductId())) {
                            try
                            {
                                AddCart_model addCart_model = new AddCart_model();
                                addCart_model.setProductCategory(product_pojo.getProductCategory());
                                addCart_model.setProductId(product_pojo.getProductId());
                                addCart_model.setProductName(product_pojo.getProductName());
                                addCart_model.set_id(product_pojo.get_id());
                                addCart_model.setUnitsOfMeasurement(product_pojo.getUnitsOfMeasurement());
                                addCart_model.setProductCategoryId(product_pojo.getProductCategoryId());
                                addCart_model.setProductDescription(product_pojo.getProductDescription());
                                addCart_model.setProductDetails(product_pojo.getProductDetails());
                                addCart_model.setUnit(product_pojo.getUnit());
                                addCart_model.setPrice(product_pojo.getSelling_price());
                                addCart_model.setUnitsOfMeasurementId(product_pojo.getUnitsOfMeasurementId());

                                if(product_pojo.getProductImages().isEmpty())
                                {
                                    addCart_model.setProductImage(product_pojo.getProductImage());
                                }
                                else
                                {
                                    addCart_model.setProductImage(product_pojo.getProductImages().get(0).getUrl());
                                }

                                addCart_model.setActive(product_pojo.getActive());
                                addCart_model.setQuantity(1);
                                addCart_model.setBrand(product_pojo.getBrand());
                                addCart_model.setAvailableQuantity(product_pojo.getAvailableQuantity());
                                addCart_model.setMRP(product_pojo.getMRP());

                                if(product_pojo.getOffer() != null)
                                {
                                    addCart_model.setOffer(product_pojo.getOffer());
                                }
                                else
                                {
                                    addCart_model.setOffer("false");
                                }

                                myApplication.setProducts(addCart_model);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        } else {
                            //Toast.makeText(activity, "Product already present in cart!!", Toast.LENGTH_SHORT).show();
                            viewHolder.count.setText(myApplication.getProductQuantity(product_pojo.getProductId()));

                        }
                    }
                });

                viewHolder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int t = Integer.parseInt(viewHolder.count.getText().toString());


                            //DashboardActivity.updateNotificationsBadge(t + 1);

                            if(t < Integer.parseInt(product_pojo.getAvailableQuantity()))
                            {
                                viewHolder.count.setText(String.valueOf(t + 1));
                                if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            }
                            else
                            {
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

                                //Toast.makeText(activity, "Sorry quantity not available in Vehicle!!!", Toast.LENGTH_SHORT).show();
                            }

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

                                if (!myApplication.DecrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            }

                            if (t == 1) {

                                myApplication.RemoveProductonZeroQuantity(product_pojo.getProductId());

                                viewHolder.holder_count.setVisibility(View.GONE);
                                viewHolder.addtoCart.setVisibility(View.VISIBLE);

                                viewHolder.count.setText("0");
                            }
                        } catch (Exception e) {

                        }
                    }
                });

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent in = new Intent(activity, DetailsActivity.class);
                            in.putExtra("_id", product_pojo.get_id());
                            in.putExtra("unitsOfMeasurement", product_pojo.getUnitsOfMeasurement());
                            in.putExtra("productCategory", product_pojo.getProductCategory());
                            in.putExtra("productCategoryId", product_pojo.getProductCategoryId());
                            in.putExtra("productName", product_pojo.getProductName());
                            in.putExtra("productDescription", product_pojo.getProductDescription());
                            in.putExtra("productDetails", product_pojo.getProductDetails());
                            in.putExtra("unit", product_pojo.getUnit());
                            in.putExtra("unitsOfMeasurementId", product_pojo.getUnitsOfMeasurementId());
                            in.putExtra("productId", product_pojo.getProductId());
                            if(product_pojo.getProductImages().isEmpty())
                            {
                                in.putExtra("productImage", product_pojo.getProductImage());
                                in.putExtra("productImages","");
                            }
                            else
                            {
                                in.putExtra("productImage", product_pojo.getProductImages().get(0).getUrl());

                                //Set the values
                                Gson gson = new Gson();
                                String jsonText = gson.toJson(product_pojo.getProductImages());
                                in.putExtra("productImages",jsonText);
                            }
                            in.putExtra("active", product_pojo.getActive());
                            in.putExtra("price",product_pojo.getSelling_price());
                            in.putExtra("brand",product_pojo.getBrand());
                            in.putExtra("quantity",product_pojo.getAvailableQuantity());
                            in.putExtra("MRP",product_pojo.getMRP());

                            if(product_pojo.getOffer() != null)
                            {
                                in.putExtra("Offer",product_pojo.getOffer());
                            }
                            else
                            {
                                in.putExtra("Offer","false");
                            }

                            //in.putExtra("quantity",String.valueOf(viewHolder.count.getText().toString().trim()));
                            startActivity(in);

                        } catch (Exception e) {
                            Log.i("Exception_MyDataAdapter", e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return convertView;
        }

        private class ViewHolder {
            TextView product_name, weight, price, count,quantity,mrp;
            SimpleDraweeView imageView;
            Button plus, minus;
            FancyButton addtoCart;
            LinearLayout holder_count;
        }
    }
}
