package com.kesari.trackingfresh.DetailPage;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class DetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, NetworkUtilsReceiver.NetworkResponseInt {

    private SliderLayout mDemoSlider;
    TextView mrp, count;
    Button plus, minus;
    FancyButton  delete;
    LinearLayout holder_count;
    FancyButton gotoCart,  checkOut;
    TextView addtoCart;
    TextView price, percent, disclaimer, related_searches, package_contents, product_description, product_category, title_productname;
    private String TAG = this.getClass().getSimpleName();
    private String productDescription = "";
    private String unitsOfMeasurement = "";
    private String productCategory = "";
    //private String __v = "";
    private String productImage = "";
    private String productImages = "";
    //private String editedAt = "";
    private String productId = "";
    private String unit = "";
    private String productPrice = "";
    //private String cuid = "";
    //private String createdBy = "";
    private String _id = "";
    private String unitsOfMeasurementId = "";
    //private String createdAt = "";
    //private String editedBy = "";
    private String productDetails = "";
    private String active = "";
    //private String slug = "";
    private String productName = "";
    private String productCategoryId = "";
    private String availableQuantity = "";
    private String brand = "";
    private String MRP = "";
    private String Offer = "";
    private NetworkUtilsReceiver networkUtilsReceiver;
    MyApplication myApplication;
    List<AddressPOJO> addressArrayList = new ArrayList<>();

    private Gson gson;
    private FetchAddressPOJO fetchAddressPOJO;
    public static int mNotificationsCount = 0;
    // ScheduledExecutorService scheduleTaskExecutor;
    FancyButton Share;
    ImageView offersImage;

    private String mfgDate = "";
    private String expDate = "";
    private String qc = "";
    private String batchNo = "";

    TextView mfgDateTxt,expiryDateTxt,qcCertTxt,batchNoTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        try {

            //Initializing toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            toolbar.setTitleTextColor(ContextCompat.getColor(DetailsActivity.this,R.color.black));
            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.porcelain));

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                IOUtils.buildAlertMessageNoGps(DetailsActivity.this);
            } else {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            myApplication = (MyApplication) getApplicationContext();
            // Getting Data from previous activity


            productDescription = getIntent().getStringExtra("productDescription");
            unitsOfMeasurement = getIntent().getStringExtra("unitsOfMeasurement");
            productCategory = getIntent().getStringExtra("productCategory");
            productImage = getIntent().getStringExtra("productImage");
            productId = getIntent().getStringExtra("productId");
            unit = getIntent().getStringExtra("unit");
            _id = getIntent().getStringExtra("_id");
            unitsOfMeasurementId = getIntent().getStringExtra("unitsOfMeasurementId");
            productDetails = getIntent().getStringExtra("productDetails");
            active = getIntent().getStringExtra("active");
            productName = getIntent().getStringExtra("productName");
            productCategoryId = getIntent().getStringExtra("productCategoryId");
            productPrice = getIntent().getStringExtra("price");
            availableQuantity = getIntent().getStringExtra("quantity");
            brand = getIntent().getStringExtra("brand");
            MRP = getIntent().getStringExtra("MRP");
            productImages = getIntent().getStringExtra("productImages");
            Offer = getIntent().getStringExtra("Offer");

            mfgDate = getIntent().getStringExtra("mfgDate");
            expDate = getIntent().getStringExtra("expDate");
            qc = getIntent().getStringExtra("qc");
            batchNo = getIntent().getStringExtra("batchNo");

            Log.i("ImageList",productImages);

            initCollapsingToolbar();
            //Image Slider
            mDemoSlider = (SliderLayout) findViewById(R.id.slider);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            offersImage = (ImageView) findViewById(R.id.offersImage);

            if(Offer.equalsIgnoreCase("true"))
            {
                offersImage.setVisibility(View.VISIBLE);
            }
            else
            {
                offersImage.setVisibility(View.GONE);
            }

           if(!productImages.isEmpty())
            {
                try
                {
                    JSONArray product = new JSONArray(productImages);

                    for(int i = 0; i < product.length(); i++)
                    {
                        JSONObject jsonObject = product.getJSONObject(i);
                        //url_maps.put("", jsonObject.getString("url"));

                        TextSliderView textSliderView = new TextSliderView(this);
                        // initialize a SliderLayout
                        textSliderView
                                .description("")
                                .image(jsonObject.getString("url"))
                                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                                .setOnSliderClickListener(this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra", "");

                        mDemoSlider.addSlider(textSliderView);
                    }
                }catch (Exception e)
                {
                    //url_maps.put("", productImage);
                    e.printStackTrace();
                }

            }
            else
            {
                HashMap<String, String> url_maps = new HashMap<String, String>();
                url_maps.put("", productImage);

                for (String name : url_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description(name)
                        .image(url_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);

                mDemoSlider.addSlider(textSliderView);
            }
            }

            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);

            //View Binding
            mrp = (TextView) findViewById(R.id.mrp);
            mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mrp.setText("₹ " + MRP);
            plus = (Button) findViewById(R.id.plus);
            minus = (Button) findViewById(R.id.minus);
            delete = (FancyButton) findViewById(R.id.delete);
            price = (TextView) findViewById(R.id.price);
            percent = (TextView) findViewById(R.id.percent);
            disclaimer = (TextView) findViewById(R.id.disclaimer);
            related_searches = (TextView) findViewById(R.id.related_searches);
            package_contents = (TextView) findViewById(R.id.package_contents);
            product_description = (TextView) findViewById(R.id.product_description);
            count = (TextView) findViewById(R.id.count);
            gotoCart = (FancyButton) findViewById(R.id.gotoCart);
            checkOut = (FancyButton) findViewById(R.id.checkOut);
            product_category = (TextView) findViewById(R.id.product_category);
            title_productname = (TextView) findViewById(R.id.title_productname);
            Share = (FancyButton) findViewById(R.id.Share);

            addtoCart = (TextView) findViewById(R.id.addtoCart);
            holder_count = (LinearLayout) findViewById(R.id.holder_count);

            mfgDateTxt = (TextView) findViewById(R.id.mfgDateTxt);
            expiryDateTxt = (TextView) findViewById(R.id.expiryDateTxt);
            qcCertTxt = (TextView) findViewById(R.id.qcCertTxt);
            batchNoTxt = (TextView) findViewById(R.id.batchNoTxt);

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            //SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");

            if(!mfgDate.isEmpty())
            {
                Date d = sdfInput.parse(mfgDate);
                String formatteddob = sdfOutput.format(d);
                mfgDateTxt.setText(formatteddob);
            }

            if(!expDate.isEmpty())
            {
                Date d = sdfInput.parse(expDate);
                String formatteddob = sdfOutput.format(d);
                expiryDateTxt.setText(formatteddob);
            }

            qcCertTxt.setText(qc);
            batchNoTxt.setText(batchNo);

            //Setting value
            product_description.setText(productDetails);
            package_contents.setText(productDescription + "," + unit + unitsOfMeasurement);
            product_category.setText(productCategory);
            title_productname.setText(productName);
            setTitle(productName);

            price.setText("₹ " + productPrice);

            /*if (!myApplication.checkifproductexists(productId)) {
                holder_count.setVisibility(View.VISIBLE);
                addtoCart.setVisibility(View.GONE);
            } else {
                holder_count.setVisibility(View.GONE);
                addtoCart.setVisibility(View.VISIBLE);
            }*/

            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBodyText = productImage + "\n\n" + productName;
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, productName);
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(intent, "Choose sharing method"));
                }
            });

            addtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder_count.setVisibility(View.VISIBLE);
                    addtoCart.setVisibility(View.GONE);
                    count.setText("1");

                    if (!myApplication.checkifproductexists(productId)) {

                        //count.setText("0");

                        AddCart_model addCart_model = new AddCart_model();
                        addCart_model.setProductCategory(productCategory);
                        addCart_model.setProductId(productId);
                        addCart_model.setProductName(productName);
                        addCart_model.set_id(_id);
                        addCart_model.setUnitsOfMeasurement(unitsOfMeasurement);
                        addCart_model.setProductCategoryId(productCategoryId);
                        addCart_model.setProductDescription(productDescription);
                        addCart_model.setProductDetails(productDetails);
                        addCart_model.setUnit(unit);
                        addCart_model.setPrice(productPrice);
                        addCart_model.setUnitsOfMeasurementId(unitsOfMeasurementId);

                        if(!productImages.isEmpty())
                        {
                            try {
                                JSONArray product = new JSONArray(productImages);
                                JSONObject jsonObject = product.getJSONObject(0);

                                addCart_model.setProductImage(jsonObject.getString("url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                        {
                            addCart_model.setProductImage(productImage);
                        }

                        addCart_model.setActive(active);
                        addCart_model.setQuantity(1);
                        addCart_model.setBrand(brand);
                        addCart_model.setAvailableQuantity(availableQuantity);
                        addCart_model.setMRP(MRP);

                        myApplication.setProducts(addCart_model);

                    } else {
                        count.setText(myApplication.getProductQuantity(productId));
                    }

                }
            });

            //Click Listeners
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int t = Integer.parseInt(count.getText().toString());


                        if (t < Integer.parseInt(availableQuantity))
                        {
                            count.setText(String.valueOf(t + 1));
                            if (!myApplication.IncrementProductQuantity(productId)) {

                            } else {

                            }

                        } else {
                            final Dialog dialog = new Dialog(DetailsActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.item_unavailable_dialog);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.show();

                            FancyButton btnCancel = (FancyButton) dialog.findViewById(R.id.btnCancel);
                            btnCancel.setText("Oops! Only " + availableQuantity + " items available!!");
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int t = Integer.parseInt(count.getText().toString());
                        if (t > 0) {
                            count.setText(String.valueOf(t - 1));

                            //DashboardActivity.updateNotificationsBadge(t - 1);

                            if (!myApplication.DecrementProductQuantity(productId)) {

                            } else {

                            }
                        }

                        if (t == 1) {

                            myApplication.RemoveProductonZeroQuantity(productId);

                            holder_count.setVisibility(View.GONE);
                            addtoCart.setVisibility(View.VISIBLE);

                            count.setText("0");
                        }
                    } catch (Exception e) {

                    }
                }
            });

           /* delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myApplication.RemoveProductonZeroQuantity(productId);
                    count.setText("0");
                }
            });*/

            gotoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailsActivity.this, AddToCart.class);
                    startActivity(intent);
                }
            });

            checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try
                    {
                        if (!myApplication.getProductsArraylist().isEmpty()) {
                            fetchUserAddress();
                        } else {
                            //Toast.makeText(DetailsActivity.this, "No Items in Cart!!", Toast.LENGTH_SHORT).show();

                            new SweetAlertDialog(DetailsActivity.this)
                                    .setTitleText("No Items in Cart!!")
                                    .show();
                        }
                    }catch (Exception e)
                    {
                        //Toast.makeText(DetailsActivity.this, "No Items in Cart!!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(DetailsActivity.this)
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
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DetailsActivity.this));

            ioUtils.getGETStringRequestHeader(DetailsActivity.this, url, params, new IOUtils.VolleyCallback() {
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
                Intent intent = new Intent(DetailsActivity.this, Add_DeliveryAddress.class);
                startActivity(intent);
            } else {

               /* addressArrayList = fetchAddressPOJO.getData();

                for (Iterator<AddressPOJO> it = addressArrayList.iterator(); it.hasNext(); ) {
                    AddressPOJO address = it.next();

                    if (address.getIsDefault().equals("true"))
                    {

                    }
                }*/

                Intent intent = new Intent(DetailsActivity.this, Default_DeliveryAddress.class);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void initCollapsingToolbar() {

        try {

            final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
            appBarLayout.setExpanded(true);

            // hiding & showing the title when toolbar expanded & collapsed
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        //collapsingToolbar.setTitle(getString(R.string.app_name));


                        collapsingToolbar.setTitle(productName);


                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle(" ");
                        isShow = false;
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hot:
                Intent intent = new Intent(DetailsActivity.this, AddToCart.class);
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
}
