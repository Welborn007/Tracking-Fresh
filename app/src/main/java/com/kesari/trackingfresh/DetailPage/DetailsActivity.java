package com.kesari.trackingfresh.DetailPage;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.R;

import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;

public class DetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener{

    private SliderLayout mDemoSlider;
    TextView discount,count;
    FancyButton plus,minus;
    Button addtoCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);

        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Tomato1", "http://cdn1-www.wholesomebabyfood.momtastic.com/assets/uploads/2015/04/tomato.jpg");
        url_maps.put("Tomato2", "https://grist.files.wordpress.com/2009/09/tomato.jpg");
        url_maps.put("Tomato3", "http://media.treehugger.com/assets/images/2012/08/Ramon-Gonzalez-Tomatoes.jpg.650x0_q70_crop-smart.jpg");
        url_maps.put("Tomato4", "http://venturesafrica.com/wp-content/uploads/2016/05/tomatoes-in-baskets.jpg");

        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);

        discount = (TextView) findViewById(R.id.discount);
        discount.setPaintFlags(discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        plus = (FancyButton) findViewById(R.id.plus);
        minus = (FancyButton) findViewById(R.id.minus);
        count = (TextView) findViewById(R.id.count);
        addtoCart = (Button) findViewById(R.id.addtoCart);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    int t = Integer.parseInt(count.getText().toString());
                    count.setText(String.valueOf(t+1));
                }
                catch (Exception e)
                {

                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int t = Integer.parseInt(count.getText().toString());
                    if(t > 0)
                    {
                        count.setText(String.valueOf(t-1));
                    }
                }catch (Exception e)
                {

                }
            }
        });

        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, AddToCart.class);
                startActivity(intent);
            }
        });
    }

    private void initCollapsingToolbar() {
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


                        collapsingToolbar.setTitle("Fresh Tomatoes");


                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}
