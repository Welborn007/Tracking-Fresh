package com.trackingfresh.customer.DeliveryAddress.DefaultDeliveryAddress;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trackingfresh.customer.AddToCart.AddCart_model;
import com.trackingfresh.customer.Cart.AddToCart;
import com.trackingfresh.customer.ConfirmOrder.ConfirmOrderActivity;
import com.trackingfresh.customer.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.trackingfresh.customer.DeliveryAddress.AddressPOJO;
import com.trackingfresh.customer.DeliveryAddress.OrderFareMainPOJO;
import com.trackingfresh.customer.DeliveryAddress.UpdateDeleteDeliveryAddress.FetchedDeliveryAddressActivity;
import com.trackingfresh.customer.Map.LocationServiceNew;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Utilities.Constants;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;
import com.trackingfresh.customer.network.MyApplication;
import com.trackingfresh.customer.network.NetworkUtils;
import com.trackingfresh.customer.network.NetworkUtilsReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.trackingfresh.customer.Utilities.IOUtils.setBadgeCount;

public class Default_DeliveryAddress extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private NetworkUtilsReceiver networkUtilsReceiver;
    FancyButton btnSubmit,btnChange,btnNew;
    //private GPSTracker gpsTracker;
    private Location Current_Origin;
    private String TAG = this.getClass().getSimpleName();
    String city_geo = "";
    String state_geo = "";
    String country_geo = "";

    String addressZero = "";
    String addressOne = "";
    String addressTwo = "";
    String Country = "";
    String FeatureName = "";
    String AdminArea = "";
    String CountryCode = "";
    String Locality = "";
    String postalCode = "";
    String subAdminArea = "";
    String subLocality = "";

    TextView name,address,city,pincode;

    List<AddressPOJO> addressArrayList = new ArrayList<>();
    List<AddCart_model> addCart_models = new ArrayList<>();

    private Gson gson;
    private FetchAddressPOJO fetchAddressPOJO;
    private OrderFareMainPOJO orderFareMainPOJO;
    boolean default_address = false;
    LinearLayout address_holder;
    CheckBox pickup;
    String OrderPlacedBy = "";

    //ScheduledExecutorService scheduleTaskExecutor;
    MyApplication myApplication;
    public static int mNotificationsCount = 0;

    boolean isPickup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default__delivery_address);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.porcelain));

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();
            myApplication = (MyApplication) getApplicationContext();

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(Default_DeliveryAddress.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            btnSubmit = (FancyButton) findViewById(R.id.btnSubmit);
            btnChange = (FancyButton) findViewById(R.id.btnChange);
            btnNew = (FancyButton) findViewById(R.id.btnNew);

            name = (TextView) findViewById(R.id.name);
            address = (TextView) findViewById(R.id.address);
            city = (TextView) findViewById(R.id.city);
            pincode = (TextView) findViewById(R.id.pincode);

            pickup = (CheckBox) findViewById(R.id.pickup);
            address_holder = (LinearLayout) findViewById(R.id.address_holder);

            pickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked)
                    {
                        //address_holder.setVisibility(View.GONE);
                        isPickup = true;
                    }
                    else
                    {
                        //address_holder.setVisibility(View.VISIBLE);
                        isPickup = false;
                    }

                }
            });

            btnNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Default_DeliveryAddress.this, Add_DeliveryAddress.class);
                    startActivity(intent);
                }
            });

            btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SetDefaultAddress();
                    Intent intent = new Intent(Default_DeliveryAddress.this, FetchedDeliveryAddressActivity.class);
                    startActivity(intent);
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(default_address)
                    {
                        if(SharedPrefUtil.getNearestRouteMainPOJO(Default_DeliveryAddress.this) != null)
                        {
                            if(!SharedPrefUtil.getNearestRouteMainPOJO(Default_DeliveryAddress.this).getData().get(0).getVehicleId().isEmpty())
                            {
                                addOrderListFromCart();
                            }
                            else
                            {
                                //Toast.makeText(Default_DeliveryAddress.this, "Sorry Vehicle Left!!", Toast.LENGTH_SHORT).show();

                                new SweetAlertDialog(Default_DeliveryAddress.this)
                                        .setTitleText("Sorry Vehicle Left!!")
                                        .show();
                            }
                        }
                        else
                        {
                            //Toast.makeText(Default_DeliveryAddress.this, "Sorry Vehicle Left!!", Toast.LENGTH_SHORT).show();

                            new SweetAlertDialog(Default_DeliveryAddress.this)
                                    .setTitleText("Sorry Vehicle Left!!")
                                    .show();
                        }
                    }
                    else
                    {
                        //FireToast.customSnackbar(Default_DeliveryAddress.this, "Default address not set!", "");

                        new SweetAlertDialog(Default_DeliveryAddress.this)
                                .setTitleText("Default address not set!")
                                .show();
                    }
                }
            });

            updateNotificationsBadge(myApplication.getProductsArraylist().size());

           /* scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

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
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(Default_DeliveryAddress.this));

            ioUtils.getGETStringRequestHeader(Default_DeliveryAddress.this, url, params, new IOUtils.VolleyCallback() {
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

            default_address = false;
            fetchAddressPOJO = gson.fromJson(Response, FetchAddressPOJO.class);

            if (fetchAddressPOJO.getData().isEmpty()) {
                Intent intent = new Intent(Default_DeliveryAddress.this, Add_DeliveryAddress.class);
                startActivity(intent);
            } else {

                addressArrayList = fetchAddressPOJO.getData();

                for (Iterator<AddressPOJO> it = addressArrayList.iterator(); it.hasNext(); ) {
                    AddressPOJO addressPOJO = it.next();

                    if (addressPOJO.isDefault())
                    {
                        OrderPlacedBy = addressPOJO.getFullName();
                        name.setText(addressPOJO.getFullName());
                        address.setText(addressPOJO.getFlat_No() + ", " + addressPOJO.getBuildingName() + ", " + addressPOJO.getLandmark());
                        city.setText(addressPOJO.getCity());
                        pincode.setText(addressPOJO.getPincode());

                        SharedPrefUtil.setDefaultLocation(Default_DeliveryAddress.this,Float.parseFloat(addressPOJO.getLatitude()),Float.parseFloat(addressPOJO.getLongitude()));

                        default_address = true;
                    }

                }

                if(!default_address)
                {
                    Intent intent = new Intent(Default_DeliveryAddress.this, FetchedDeliveryAddressActivity.class);
                    intent.putExtra("default_address","false");
                    startActivity(intent);
                    //FireToast.customSnackbar(Default_DeliveryAddress.this, "Default address not set!", "");

                    new SweetAlertDialog(Default_DeliveryAddress.this)
                            .setTitleText("Default address not set!")
                            .show();
                    default_address = false;
                }

            }

        } catch (Exception e) {
            //Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void addOrderListFromCart()
    {
        try
        {

            String url = Constants.GetFare ;

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
                    cartItemsObjedct.put("productImage",myApplication.getProductsArraylist().get(i).getProductImage());
                    //cartItemsObjedct.put("active",myApplication.getProductsArraylist().get(i).getActive());
                    cartItemsArray.put(cartItemsObjedct);
                }

                /*postObject.put("otp", OTP);
                postObject.put("mobileNo", MobileNo);
                postObject.put("id", SharedPrefUtil.getUser(Default_DeliveryAddress.this).getData().get_id());
*/
                postObject.put("orders",cartItemsArray);
                //postObject.put("total_price","1100");
                postObject.put("vehicleId",SharedPrefUtil.getNearestRouteMainPOJO(Default_DeliveryAddress.this).getData().get(0).getVehicleId());
                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(Default_DeliveryAddress.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(Default_DeliveryAddress.this, url,params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    OrderSendResponse(result);
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

    private void OrderSendResponse(String Response)
    {
        try
        {
            orderFareMainPOJO = gson.fromJson(Response, OrderFareMainPOJO.class);

            JSONObject jsonObject = new JSONObject(Response);

            //String Message = jsonObject.getString("message");

            if(!orderFareMainPOJO.getData().getOrders().isEmpty())
            {
                Intent intent = new Intent(Default_DeliveryAddress.this, ConfirmOrderActivity.class);
                intent.putExtra("confirmOrder",Response);
                intent.putExtra("OrderPlacedBy",OrderPlacedBy);
                intent.putExtra("isPickup",isPickup);
                startActivity(intent);
                //finish();

                //myApplication.removeProductsItems();
            }
            else
            {
                //FireToast.customSnackbar(Default_DeliveryAddress.this, "No Products Added!!!", "");

                new SweetAlertDialog(Default_DeliveryAddress.this)
                        .setTitleText("No Products Added!!!")
                        .show();
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserAddress();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_tocart, menu);

        MenuItem item = menu.findItem(R.id.menu_hot);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        setBadgeCount(this, icon, mNotificationsCount);

///        BitmapDrawable iconBitmap = (BitmapDrawable) item.getIcon();
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
                Intent intent = new Intent(Default_DeliveryAddress.this, AddToCart.class);
                startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                Intent intent1 = new Intent(Default_DeliveryAddress.this, AddToCart.class);
                startActivity(intent1);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Default_DeliveryAddress.this, AddToCart.class);
        startActivity(intent);
    }

    public static void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
    }

    private void SetDefaultAddress()
    {

        try
        {

            // Create custom dialog object
            final Dialog dialog = new Dialog(Default_DeliveryAddress.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_default_address);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            TextView delivery_text;
            EditText name,email,mobile,flat_no,building_name,landmark,city,state,pincode;
            FancyButton confirmAddress;

            delivery_text = (TextView) dialog.findViewById(R.id.delivery_text);
            name = (EditText) dialog.findViewById(R.id.name);
            email = (EditText) dialog.findViewById(R.id.email);
            mobile = (EditText) dialog.findViewById(R.id.mobile);
            flat_no = (EditText) dialog.findViewById(R.id.flat_no);
            building_name = (EditText) dialog.findViewById(R.id.building_name);
            landmark = (EditText) dialog.findViewById(R.id.landmark);
            city = (EditText) dialog.findViewById(R.id.city);
            state = (EditText) dialog.findViewById(R.id.state);
            pincode = (EditText) dialog.findViewById(R.id.pincode);

            confirmAddress = (FancyButton) dialog.findViewById(R.id.confirmAddress);

            delivery_text.setText("Set new Default Delivery Address");
            name.setText(SharedPrefUtil.getUser(Default_DeliveryAddress.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(Default_DeliveryAddress.this).getData().getLastName());
            email.setText(SharedPrefUtil.getUser(Default_DeliveryAddress.this).getData().getEmailId());

            //gpsTracker = new GPSTracker(Default_DeliveryAddress.this);

            Current_Origin = SharedPrefUtil.getLocation(Default_DeliveryAddress.this);

            Double Lat = Current_Origin.getLatitude();
            Double Long = Current_Origin.getLongitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(Lat, Long, 1);

                addressZero = addresses.get(0).getAddressLine(0);
                addressOne = addresses.get(0).getAddressLine(1);
                addressTwo = addresses.get(0).getAddressLine(2);
                Country = addresses.get(0).getCountryName();
                FeatureName = addresses.get(0).getFeatureName();
                AdminArea = addresses.get(0).getAdminArea();
                CountryCode = addresses.get(0).getCountryCode();
                Locality = addresses.get(0).getLocality();
                postalCode = addresses.get(0).getPostalCode();
                subAdminArea = addresses.get(0).getSubAdminArea();
                subLocality = addresses.get(0).getSubLocality();

                city_geo = Locality;
                state_geo = AdminArea;
                country_geo = Country;

                pincode.setText(postalCode);
                city.setText(subLocality + "," + city_geo);
                state.setText(state_geo);

                Log.i("addressZero",addressZero);
                Log.i("addressOne",addressOne);
                Log.i("addressTwo",addressTwo);
                Log.i("Country",Country);
                Log.i("FeatureName",FeatureName);
                Log.i("AdminArea",AdminArea);
                Log.i("CountryCode",CountryCode);
                Log.i("Locality",Locality);
                Log.i("postalCode",postalCode);
                Log.i("subAdminArea",subAdminArea);
                Log.i("subLocality",subLocality);
                Log.i("city_geo",city_geo);
                Log.i("state_geo",state_geo);
                Log.i("country_geo",country_geo);

            } catch (Exception e) {
                e.printStackTrace();
            }

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = 1500;
            window.setAttributes(lp);

            dialog.show();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);
            //scheduleTaskExecutor.shutdown();

           /* if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }*/

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
               /* FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
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
