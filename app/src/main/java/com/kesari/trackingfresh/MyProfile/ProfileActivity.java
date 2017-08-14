package com.kesari.trackingfresh.MyProfile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kesari.trackingfresh.BuildConfig;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.DashBoard.VerifyMobilePOJO;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.Default_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.FetchAddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.UpdateDeleteDeliveryAddress.FetchedDeliveryAddressActivity;
import com.kesari.trackingfresh.Login.ProfileMain;
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
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.kesari.trackingfresh.Utilities.IOUtils.setBadgeCount;

public class ProfileActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    TextView Phonenumber,Email,address,customer_name;
    ImageView photo_edit,profile_edit;
    CircleImageView profile_image;

    private Gson gson;
    private ProfileMain profileMain;
    Dialog dialog;
    private File dest;
    String filename;
    //ScheduledExecutorService scheduleTaskExecutor;
    MyApplication myApplication;
    public static int mNotificationsCount = 0;
    TextView refferal;
    LinearLayout referral_holder,phoneHolder;
    FancyButton Share,verifiedStatus;
    VerifyMobilePOJO verifyMobilePOJO;
    private FetchAddressPOJO fetchAddressPOJO;
    List<AddressPOJO> addressArrayList = new ArrayList<>();
    boolean default_address = false;

    TextView city,pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            gson = new Gson();

            Phonenumber = (TextView) findViewById(R.id.Phonenumber);
            Email = (TextView) findViewById(R.id.Email);
            customer_name = (TextView) findViewById(R.id.customer_name);

            photo_edit = (ImageView) findViewById(R.id.photo_edit);
            profile_edit = (ImageView) findViewById(R.id.profile_edit);
            profile_image = (CircleImageView) findViewById(R.id.profile_image);
            referral_holder = (LinearLayout) findViewById(R.id.referral_holder);
            refferal = (TextView) findViewById(R.id.refferal);
            Share = (FancyButton) findViewById(R.id.Share);
            verifiedStatus = (FancyButton) findViewById(R.id.verifiedStatus);
            phoneHolder = (LinearLayout) findViewById(R.id.phoneHolder);

            address = (TextView) findViewById(R.id.address);
            city = (TextView) findViewById(R.id.city);
            pincode = (TextView) findViewById(R.id.pincode);

            getProfileData();

            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareBodyText = "https://play.google.com/store/apps/details?id=com.kesari.trackingfresh" + "\n\n" + "Referral Code - " + SharedPrefUtil.getUser(ProfileActivity.this).getData().getReferralCode();
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Referral Code");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(intent, "Choose sharing method"));
                }
            });

            profile_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditProfile();
                }
            });

            photo_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(ProfileActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            fetchUserAddress();
            myApplication = (MyApplication) getApplicationContext();

            updateNotificationsBadge(myApplication.getProductsArraylist().size());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void fetchUserAddress() {
        try {

            String url = Constants.FetchAddress;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ProfileActivity.this));

            ioUtils.getGETStringRequestHeader(ProfileActivity.this, url, params, new IOUtils.VolleyCallback() {
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

            default_address = false;
            fetchAddressPOJO = gson.fromJson(Response, FetchAddressPOJO.class);

            if (fetchAddressPOJO.getData().isEmpty()) {
                Intent intent = new Intent(ProfileActivity.this, Add_DeliveryAddress.class);
                startActivity(intent);
            } else {

                addressArrayList = fetchAddressPOJO.getData();

                for (Iterator<AddressPOJO> it = addressArrayList.iterator(); it.hasNext(); ) {
                    AddressPOJO addressPOJO = it.next();

                    if (addressPOJO.isDefault())
                    {
                        address.setText(addressPOJO.getFlat_No() + ", " + addressPOJO.getBuildingName() + ", " + addressPOJO.getLandmark());
                        city.setText(addressPOJO.getCity());
                        pincode.setText(addressPOJO.getPincode());

                        default_address = true;
                    }

                }

                if(!default_address)
                {

                    default_address = false;
                }

            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ProfileActivity.this));

            ioUtils.getPOSTStringRequestHeader(ProfileActivity.this,Constants.Profile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);
                    profileDataResponse(result);
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

            if(SharedPrefUtil.getUser(ProfileActivity.this).getData().getMobileNo() != null)
            {
                phoneHolder.setVisibility(View.VISIBLE);
                if(!SharedPrefUtil.getUser(ProfileActivity.this).getData().getMobileNo().isEmpty())
                {
                    phoneHolder.setVisibility(View.VISIBLE);
                    Phonenumber.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getMobileNo());
                }
                else
                {
                    phoneHolder.setVisibility(View.GONE);
                }
            }
            else
            {
                phoneHolder.setVisibility(View.GONE);
            }

            Email.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getEmailId());
            customer_name.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(ProfileActivity.this).getData().getLastName());

            if(SharedPrefUtil.getUser(ProfileActivity.this).getData() != null)
            {
                if(!SharedPrefUtil.getUser(ProfileActivity.this).getData().getReferralCode().isEmpty())
                {
                    refferal.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getReferralCode());
                    referral_holder.setVisibility(View.VISIBLE);
                    getVerifiedMobileNumber();
                }
                else
                {
                    referral_holder.setVisibility(View.GONE);
                }
            }

            if(SharedPrefUtil.getUser(ProfileActivity.this).getData().getProfileImage() != null)
            {
                Picasso
                        .with(ProfileActivity.this)
                        .load(SharedPrefUtil.getUser(ProfileActivity.this).getData().getProfileImage())
                        .into(profile_image);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getVerifiedMobileNumber()
    {
        try
        {

            String url = Constants.VerifyMobile + SharedPrefUtil.getUser(ProfileActivity.this).getData().get_id();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ProfileActivity.this));

            ioUtils.getGETStringRequestHeader(ProfileActivity.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    VerifyResponse(result);

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
                verifiedStatus.setVisibility(View.GONE);
            }
            else if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile not Verified"))
            {
                verifiedStatus.setVisibility(View.GONE);
            }
            else if(verifyMobilePOJO.getMessage().equalsIgnoreCase("Mobile Verified"))
            {
                verifiedStatus.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void EditProfile()
    {

        try
        {

            // Create custom dialog object
            dialog = new Dialog(ProfileActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_edit_profile);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            TextView delivery_text;
            final EditText first_name,last_name,email,referral_code/*,mobile,flat_no,building_name,landmark,city,state,pincode*/;
            FancyButton confirmAddress;

            delivery_text = (TextView) dialog.findViewById(R.id.delivery_text);
            first_name = (EditText) dialog.findViewById(R.id.first_name);
            last_name = (EditText) dialog.findViewById(R.id.last_name);
            email = (EditText) dialog.findViewById(R.id.email);
            referral_code = (EditText) dialog.findViewById(R.id.referral_code);
            //mobile = (EditText) dialog.findViewById(R.id.mobile);
           /* flat_no = (EditText) dialog.findViewById(R.id.flat_no);
            building_name = (EditText) dialog.findViewById(R.id.building_name);
            landmark = (EditText) dialog.findViewById(R.id.landmark);
            city = (EditText) dialog.findViewById(R.id.city);
            state = (EditText) dialog.findViewById(R.id.state);
            pincode = (EditText) dialog.findViewById(R.id.pincode);*/

            confirmAddress = (FancyButton) dialog.findViewById(R.id.confirmAddress);

            delivery_text.setText("Edit Profile");
            first_name.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getFirstName());
            last_name.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getLastName());
            email.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getEmailId());
            //mobile.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getMobileNo());

            confirmAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String FirstName = first_name.getText().toString().trim();
                    String LastName = last_name.getText().toString().trim();
                    String Email = email.getText().toString().trim();

                    if(!FirstName.isEmpty() && !LastName.isEmpty() && !Email.isEmpty())
                    {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                        {
                            updateCustomerProfile(SharedPrefUtil.getUser(ProfileActivity.this).getData().get_id(),FirstName,LastName,Email);
                        }
                        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                            //Toast.makeText(RegisterActivity.this, getString(R.string.proper_email), Toast.LENGTH_SHORT).show();
                            email.setError(getString(R.string.proper_email));
                        }
                    }
                    else if (FirstName.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.first_name), Toast.LENGTH_SHORT).show();
                        first_name.setError(getString(R.string.first_name));
                    } else if (LastName.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.last_name), Toast.LENGTH_SHORT).show();
                        last_name.setError(getString(R.string.last_name));
                    } else if (Email.isEmpty()) {
                        //Toast.makeText(RegisterActivity.this, getString(R.string.email_id), Toast.LENGTH_SHORT).show();
                        email.setError(getString(R.string.email_id));
                    }
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void updateCustomerProfile(String CustomerID, String firstName,String lastName,String emailID) {
        try {

            String url = Constants.ProfileEdit + CustomerID;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("firstName", firstName);
                postObject.put("lastName",lastName);
                postObject.put("emailId",emailID);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ProfileActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(ProfileActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    updateCustomerResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void updateCustomerResponse(String Response)
    {
        try
        {
            profileMain = gson.fromJson(Response, ProfileMain.class);

            if(profileMain.getMessage().equalsIgnoreCase("Update Successfully!"))
            {
                SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

                Phonenumber.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getMobileNo());
                Email.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getEmailId());
                customer_name.setText(SharedPrefUtil.getUser(ProfileActivity.this).getData().getFirstName() + " " + SharedPrefUtil.getUser(ProfileActivity.this).getData().getLastName());

                dialog.dismiss();
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

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    filename = SharedPrefUtil.getUser(ProfileActivity.this).getData().get_id() + "_" + timeStamp + ".png";
                    File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"TKF");

                    if (!sd.exists()) {
                        if (!sd.mkdirs()) {
                            Log.e("TravellerLog :: ", "Problem creating Image folder");
                        }
                    }

                    dest = new File(sd, filename);

                    Uri photoURI = FileProvider.getUriForFile(ProfileActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            dest);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

                    startActivityForResult(intent, 1);


                }

                else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);



                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                try {

                    Bitmap bitmap;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                    bitmap = BitmapFactory.decodeFile(dest.getAbsolutePath(),

                            bitmapOptions);

                    updateImageDialog();


                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {


                Uri selectedImage = data.getData();
                // h=1;
                //imgui = selectedImage;
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);
                dest = new File(picturePath);

                c.close();

                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                updateImageDialog();

                Log.w("path of image from gallery......******************.........", picturePath + "");

                //a.setImageBitmap(thumbnail);

            }

        }
    }

    private void updateImageDialog()
    {

        final CharSequence[] options = { "Upload","Cancel" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setTitle("Are You Sure?");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Upload"))
                {
                    new ImageUploadTask().execute();

                    //profile_image.setImageBitmap(bitmap);

                }
                else if (options[item].equals("Cancel")) {

                  dialog.cancel();
                }

            }

        });

        builder.show();
    }

    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = Constants.ProfileImagePath;

        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(webAddressToPost);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);


                entity.addPart("file", new FileBody(dest));
                entity.addPart("path", new StringBody("profile"));

                //entity.addPart("someOtherStringToSend", new StringBody("your string here"));

                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                OutputStream os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();


                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return readStream(conn.getInputStream());
                }


            } catch (Exception e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }
            return null;
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            Log.i("ImagePath",result);


            try {
                JSONObject jsonObject = new JSONObject(result);

                String message = jsonObject.getString("message");
                String url = jsonObject.getString("url");

                if(message.equalsIgnoreCase("uploaded"))
                {
                    Toast.makeText(getApplicationContext(), "file uploaded",
                            Toast.LENGTH_LONG).show();
                    updateCustomerProfileImage(SharedPrefUtil.getUser(ProfileActivity.this).getData().get_id(),url);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "file uploaded failed",
                            Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void updateCustomerProfileImage(String CustomerID, String profileImage) {
        try {

            String url = Constants.ProfileEdit + CustomerID;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("profileImage", profileImage);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(ProfileActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(ProfileActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    updateCustomerImageResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void updateCustomerImageResponse(String Response)
    {
        try
        {
            profileMain = gson.fromJson(Response, ProfileMain.class);

            if(profileMain.getMessage().equalsIgnoreCase("Update Successfully!"))
            {
                SharedPrefUtil.setUser(getApplicationContext(), Response.toString());

                Picasso
                        .with(ProfileActivity.this)
                        .load(SharedPrefUtil.getUser(ProfileActivity.this).getData().getProfileImage())
                        .into(profile_image);

                dialog.dismiss();
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
                Intent intent = new Intent(ProfileActivity.this, AddToCart.class);
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
