package com.trackingfresh.customer.Splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.trackingfresh.customer.BuildConfig;
import com.trackingfresh.customer.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.trackingfresh.customer.Login.LoginActivity;
import com.trackingfresh.customer.Map.LocationServiceNew;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Register.RegisterActivity;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewSplash extends AppCompatActivity {

    private static final int SPLASH_PERMISSION = 101;
    private static final int SPLASH_PERMISSION_LOGIN = 102;
    private static final int SPLASH_PERMISSION_REG = 103;
    Button btnLogin,login;
    private String TAG = this.getClass().getSimpleName();

    public static String versionName ="";
    public static String versionCode ="";
    Bitmap bitmap;
//    private PermissionListener permissionListener;
    String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_splash);

        if (hasPermissions(permissions, SPLASH_PERMISSION)) {
            checkFirstRun();

        }
        Button buttonSignUp= (Button) findViewById(R.id.buttonSignUp);
        Button buttonLogin= (Button) findViewById(R.id.buttonLogin);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (hasPermissions(permissions, SPLASH_PERMISSION_REG)) {
                    Intent startMainActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                    startMainActivity.putExtra("Type","simple");
                    startActivity(startMainActivity);
//                }

            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent startMainActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(startMainActivity);
                finish();*/
//                if (hasPermissions(permissions, SPLASH_PERMISSION_LOGIN)) {
                    startApp();
//                }

            }
        });


    }

    public void startApp()
    {
        if (SharedPrefUtil.getToken(NewSplash.this) != null) {
            if(!SharedPrefUtil.getToken(NewSplash.this).isEmpty())
            {
                Intent startMainActivity = new Intent(getApplicationContext(),CheckVehicleActivity.class);
                startActivity(startMainActivity);
                finish();
            }
            else
            {
                Intent startMainActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(startMainActivity);
                finish();
            }
        }
        else
        {
            Intent startMainActivity = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(startMainActivity);
            finish();
        }
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            startApp();
            checkVersion();
            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void checkVersion()
    {
        try {
            versionCode = String.valueOf(getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode);

            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;

            Log.d("Version Code",versionCode);
            Log.d("Version Name",versionName);

            new GetVersionCode().execute();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

/*
    @Override
    public void onPermissionDenied() {
        finishAffinity();
    }
*/

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + NewSplash.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (!versionName.equalsIgnoreCase(onlineVersion)) {
                    //show dialog
                    //displayVersionAlert("This is not a updated version. Please update this version");
                    //Toast.makeText(getApplicationContext(), "Version not updated", Toast.LENGTH_LONG).show();

                    sendNotification("This is not a updated version. Please update this version","");
                }
                else {
                    //startApp();
                    //Toast.makeText(getApplicationContext(), "Version updated", Toast.LENGTH_LONG).show();
                }

            }
            Log.d("update", "Current version " + versionName + " playstore version " + onlineVersion);
        }
    }

    private void sendNotification(String messageBody,String Image) {
        try
        {

            String url = "https://play.google.com/store/apps/details?id=com.trackingfresh.customer";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            //startActivity(i);

            /*Intent intent = new Intent(NewSplash.this, NewSplash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
            PendingIntent pendingIntent = PendingIntent.getActivity(NewSplash.this, 0, i,
                    PendingIntent.FLAG_ONE_SHOT);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NewSplash.this)
                    .setContentTitle("Tracking Fresh")
                    //.setContentText(messageBody)
                    //.setLargeIcon(largeIcon)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_tkf);
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }

            if(!Image.isEmpty())
            {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(Image).getContent());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 350, 150, false);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)).setSubText(messageBody);
            }
            else
            {
                //notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)).setContentText(messageBody);
                //Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.tracking_banner);
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()).setContentText(messageBody);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Added for permission*/
    //Permissions
    public boolean hasPermissions(String[] flags, int requestCode) {
//        this.permissionListener = permissionListener;
        List<String> permissionToBeRequested = new ArrayList<>();
        for (String flag : flags) {
            if (!(ContextCompat.checkSelfPermission(this, flag) == PackageManager.PERMISSION_GRANTED)) {
                permissionToBeRequested.add(flag);
            }
        }
        if (permissionToBeRequested.isEmpty())
            return true;
        else {
            askForPermission(permissionToBeRequested, requestCode);
            return false;
        }
    }
/*
    public interface PermissionListener {
        void onPermissionDenied();
    }*/
    private void askForPermission(List<String> permissionToBeRequested, int requestCode) {
        String[] stringPermissions = new String[permissionToBeRequested.size()];
        stringPermissions = (String[]) permissionToBeRequested.toArray(stringPermissions);
//        LogUtil.info(TAG, "stringPermissions : " + stringPermissions.length);
        ActivityCompat.requestPermissions(this, stringPermissions, requestCode);
    }

    public void askForPermission(String[] permissionToBeRequested, int requestCode) {
//        LogUtil.info(TAG, "stringPermissions : " + permissionToBeRequested.length);
        ActivityCompat.requestPermissions(this, permissionToBeRequested, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> deniedPermissions = new ArrayList<>();
        if (requestCode == SPLASH_PERMISSION) {
            for (int i = 0; i <= permissions.length - 1; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (!deniedPermissions.isEmpty())
                isUserDeniedPermissionForEver(deniedPermissions);
            if (permissions.length == grantResults.length) {
                checkFirstRun();
            }
        }else {
            for (int i = 0; i <= permissions.length - 1; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (!deniedPermissions.isEmpty())
                isUserDeniedPermissionForEver(deniedPermissions);
            if (permissions.length == grantResults.length) {
                checkFirstRun();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void isUserDeniedPermissionForEver(List<String> permissions) {
        String permissionsName = "";
        for (String permission : permissions) {
            if (!shouldShowRequestPermissionRationale(permission)) {
                try {
                    PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(permission, 0);
                    String groupName = permissionInfo.group.split("\\.")[(permissionInfo.group.split("\\.").length - 1)];
                    permissionsName = permissionsName.isEmpty() ? groupName : permissionsName + " And " + groupName;
                } catch (PackageManager.NameNotFoundException e) {
//                    LogUtil.printException(e);
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(NewSplash.this);
        builder.setMessage("All Permissions should be granted to use Tracking Fresh")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        goToPermissionSettings();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        permissionListener.onPermissionDenied();

                        dialog.cancel();
                        finishAffinity();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

      /*  DialogUtils.showAlert(this, getResString(R.string.permission), getResString(R.string.need_permission), getResString(R.string.okay), getResString(R.string.deny), new DialogListener() {
                    @Override
                    public void onPositiveClicked() {
                        MyApplication.LOOKING_FOR_PERMISSION = true;
                        goToPermissionSettings();
                    }

                    @Override
                    public void onNegativeClicked() {
                        permissionListener.onPermissionDenied();
                    }
                }
        );*/
    }

    public void goToPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


}
