package com.kesari.trackingfresh.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.kesari.trackingfresh.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

public class SplashActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(3000);

                    startApp();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    public void startApp()
    {
        if (SharedPrefUtil.getUser(SplashActivity.this) != null) {
            if(!SharedPrefUtil.getUser(SplashActivity.this).getData().getMobileNo().isEmpty())
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
}
