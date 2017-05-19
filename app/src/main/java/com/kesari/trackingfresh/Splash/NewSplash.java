package com.kesari.trackingfresh.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kesari.trackingfresh.Login.LoginActivity;
import com.kesari.trackingfresh.ProductPage.DashboardActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

public class NewSplash extends AppCompatActivity {

    Button btnLogin,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_splash);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApp();
            }
        });


    }

    public void startApp()
    {
        if (SharedPrefUtil.getUser(NewSplash.this) != null) {
            if(!SharedPrefUtil.getUser(NewSplash.this).getUser().getEmailId().isEmpty())
            {
                Intent startMainActivity = new Intent(getApplicationContext(),DashboardActivity.class);
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
