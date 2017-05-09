package com.kesari.trackingfresh.DeliveryAddress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kesari.trackingfresh.R;

public class Add_DeliveryAddress extends AppCompatActivity {

    Button confirmAddress;
    EditText name,email,mobile,city,state,pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confirmAddress = (Button) findViewById(R.id.confirmAddress);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        pincode = (EditText) findViewById(R.id.pincode);

        confirmAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_DeliveryAddress.this,Default_DeliveryAddress.class);
                startActivity(intent);
            }
        });
    }
}
