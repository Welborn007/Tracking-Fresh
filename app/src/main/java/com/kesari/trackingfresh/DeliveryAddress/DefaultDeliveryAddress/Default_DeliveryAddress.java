package com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.ConfirmOrder.ConfirmOrderActivity;
import com.kesari.trackingfresh.ConfirmOrder.OrderAddPojo;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.UpdateDeleteDeliveryAddress.FetchedDeliveryAddressActivity;
import com.kesari.trackingfresh.Map.LocationServiceNew;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Default_DeliveryAddress extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private NetworkUtilsReceiver networkUtilsReceiver;
    Button btnSubmit,btnChange,btnNew;
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
    private OrderAddPojo orderAddPojo;
    boolean default_address = false;
    MyApplication myApplication;
    LinearLayout address_holder;
    CheckBox pickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default__delivery_address);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            btnChange = (Button) findViewById(R.id.btnChange);
            btnNew = (Button) findViewById(R.id.btnNew);

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
                        address_holder.setVisibility(View.GONE);
                    }
                    else
                    {
                        address_holder.setVisibility(View.VISIBLE);
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
                        addOrderListFromCart();
                    }
                    else
                    {
                        FireToast.customSnackbar(Default_DeliveryAddress.this, "Default address not set!", "");
                    }
                }
            });

           /* for (int i = 0; i < myApplication.getProductsArraylist().size(); i++) {
                AddCart_model addCart_model = new AddCart_model();
                addCart_model.setProductId(myApplication.getProductsArraylist().get(i).getProductId());
                addCart_model.setQuantity(myApplication.getProductsArraylist().get(i).getQuantity());
                addCart_model.setPrice("100");
                Log.i("product_id", myApplication.getProductsArraylist().get(i).getProductId());
                addCart_model.setActive(myApplication.getProductsArraylist().get(i).getActive());
                addCart_models.add(addCart_model);
            }*/

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
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void fetchUserAddressResponse(String Response) {
        try {

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
                        name.setText(addressPOJO.getFullName());
                        address.setText(addressPOJO.getFlat_No() + ", " + addressPOJO.getBuildingName() + ", " + addressPOJO.getLandmark());
                        city.setText(addressPOJO.getCity());
                        pincode.setText(addressPOJO.getPincode());

                        default_address = true;
                    }

                }

                if(!default_address)
                {
                    Intent intent = new Intent(Default_DeliveryAddress.this, FetchedDeliveryAddressActivity.class);
                    intent.putExtra("default_address","false");
                    startActivity(intent);
                    FireToast.customSnackbar(Default_DeliveryAddress.this, "Default address not set!", "");
                }

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
                    cartItemsObjedct.put("price","100");
                    Log.i("product_id", myApplication.getProductsArraylist().get(i).getProductId());
                    cartItemsObjedct.put("active",myApplication.getProductsArraylist().get(i).getActive());
                    cartItemsArray.put(cartItemsObjedct);
                }

                /*postObject.put("otp", OTP);
                postObject.put("mobileNo", MobileNo);
                postObject.put("id", SharedPrefUtil.getUser(Default_DeliveryAddress.this).getData().get_id());
*/
                postObject.put("order",cartItemsArray);
                postObject.put("total_price","1100");
                postObject.put("vehicleNo","vehicleNo1");
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

            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(!orderAddPojo.getMessage().get_id().isEmpty())
            {
                Intent intent = new Intent(Default_DeliveryAddress.this, ConfirmOrderActivity.class);
                intent.putExtra("confirmOrder",Response);
                startActivity(intent);
                finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            Button confirmAddress;

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

            confirmAddress = (Button) dialog.findViewById(R.id.confirmAddress);

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
