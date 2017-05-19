package com.kesari.trackingfresh.DetailPage;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
    FancyButton plus,minus,delete;
    Button addtoCart;
    TextView price,percent,disclaimer,related_searches,package_contents,product_description,product_category,title_productname;

    private String productDescription = "";
    private String unitsOfMeasurement = "";
    private String productCategory = "";
    private String __v = "";
    private String productImage = "";
    private String editedAt = "";
    private String productId = "";
    private String unit = "";
    private String cuid = "";
    private String createdBy = "";
    private String _id = "";
    private String unitsOfMeasurementId ="";
    private String createdAt = "";
    private String editedBy = "";
    private String productDetails = "";
    private String active = "";
    private String slug = "";
    private String productName = "";
    private String productCategoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Initializing toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try
        {

            // Getting Data from previous activity
            productDescription = getIntent().getStringExtra("productDescription");
            unitsOfMeasurement = getIntent().getStringExtra("unitsOfMeasurement");
            productCategory = getIntent().getStringExtra("productCategory");
            __v = getIntent().getStringExtra("__v");
            productImage = getIntent().getStringExtra("productImage");
            editedAt = getIntent().getStringExtra("editedAt");
            productId = getIntent().getStringExtra("productId");
            unit = getIntent().getStringExtra("unit");
            cuid = getIntent().getStringExtra("cuid");
            createdBy = getIntent().getStringExtra("createdBy");
            _id = getIntent().getStringExtra("_id");
            unitsOfMeasurementId = getIntent().getStringExtra("unitsOfMeasurementId");
            createdAt = getIntent().getStringExtra("createdAt");
            editedBy = getIntent().getStringExtra("editedBy");
            productDetails = getIntent().getStringExtra("productDetails");
            active = getIntent().getStringExtra("active");
            slug = getIntent().getStringExtra("slug");
            productName = getIntent().getStringExtra("productName");
            productCategoryId = getIntent().getStringExtra("productCategoryId");

            initCollapsingToolbar();
            //Image Slider
            mDemoSlider = (SliderLayout)findViewById(R.id.slider);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);

            HashMap<String,String> url_maps = new HashMap<String, String>();
            /*url_maps.put("Tomato1", "http://cdn1-www.wholesomebabyfood.momtastic.com/assets/uploads/2015/04/tomato.jpg");
            url_maps.put("Tomato2", "https://grist.files.wordpress.com/2009/09/tomato.jpg");
            url_maps.put("Tomato3", "http://media.treehugger.com/assets/images/2012/08/Ramon-Gonzalez-Tomatoes.jpg.650x0_q70_crop-smart.jpg");
            url_maps.put("Tomato4", "http://venturesafrica.com/wp-content/uploads/2016/05/tomatoes-in-baskets.jpg");*/

            url_maps.put("Tomato1", productImage);
            url_maps.put("Tomato2", productImage);
            url_maps.put("Tomato3", productImage);
            url_maps.put("Tomato4", productImage);

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

            //View Binding
            discount = (TextView) findViewById(R.id.discount);
            discount.setPaintFlags(discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            plus = (FancyButton) findViewById(R.id.plus);
            minus = (FancyButton) findViewById(R.id.minus);
            delete = (FancyButton) findViewById(R.id.delete);
            price = (TextView) findViewById(R.id.price);
            percent = (TextView) findViewById(R.id.percent);
            disclaimer = (TextView) findViewById(R.id.disclaimer);
            related_searches = (TextView) findViewById(R.id.related_searches);
            package_contents = (TextView) findViewById(R.id.package_contents);
            product_description = (TextView) findViewById(R.id.product_description);
            count = (TextView) findViewById(R.id.count);
            addtoCart = (Button) findViewById(R.id.addtoCart);
            product_category = (TextView) findViewById(R.id.product_category);
            title_productname = (TextView) findViewById(R.id.title_productname);

            //Setting value
            product_description.setText(productDetails);
            package_contents.setText(productDescription +  "," + unit + unitsOfMeasurement);
            product_category.setText(productCategory);
            title_productname.setText(productName);

            //Click Listeners
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
        catch (Exception e)
        {
            Log.i("DetailsActi_Oncreate",e.getMessage());
        }
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


                        collapsingToolbar.setTitle(productName);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
