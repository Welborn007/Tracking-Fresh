package com.kesari.trackingfresh.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kesari.trackingfresh.CheckNearestVehicleAvailability.CheckVehicleActivity;
import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Register.RegisterActivity;
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
        Button buttonSignUp= (Button) findViewById(R.id.buttonSignUp);
        Button buttonLogin= (Button) findViewById(R.id.buttonLogin);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMainActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startMainActivity.putExtra("Type","simple");
                startActivity(startMainActivity);
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMainActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(startMainActivity);
                finish();
            }
        });
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
