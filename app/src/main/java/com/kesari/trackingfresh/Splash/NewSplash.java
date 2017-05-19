package com.kesari.trackingfresh.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.R;

public class NewSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_splash);
    }

    public void startApp()
    {
        Intent startMainActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(startMainActivity);
        finish();
    }
}
