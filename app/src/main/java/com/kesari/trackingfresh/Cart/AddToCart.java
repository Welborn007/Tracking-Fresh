package com.kesari.trackingfresh.Cart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.DeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.network.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddToCart extends AppCompatActivity {

    GridView gridview;
    private MyDataAdapter myDataAdapter;
    //List<Product_POJO> product_pojos = new ArrayList<>();
    TextView cart_count;
    Button checkOut;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridview = (GridView) findViewById(R.id.list);
        cart_count = (TextView) findViewById(R.id.cart_count);
        checkOut = (Button) findViewById(R.id.checkOut);

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddToCart.this, Add_DeliveryAddress.class);
                startActivity(intent);
            }
        });

        myApplication = (MyApplication) getApplicationContext();

        getProductData();
    }

    public void getProductData()
    {
        try {
            /*JSONArray jsonArray = new JSONArray(loadProductJSONFromAsset());


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                Product_POJO js = new Product_POJO();

                String product_name = jo_inside.getString("product_name");
                String images = jo_inside.getString("images");
                String id = jo_inside.getString("id");
                String kilo = jo_inside.getString("kilo");
                String Rs = jo_inside.getString("Rs");

                js.setId(id);
                js.setImages(images);
                js.setProduct_name(product_name);
                js.setKilo(kilo);
                js.setRs(Rs);

                product_pojos.add(js);

            }

            Collections.shuffle(product_pojos);
*/



            myDataAdapter = new MyDataAdapter(myApplication.getProductsArraylist(),AddToCart.this);
            gridview.setAdapter(myDataAdapter);
            myDataAdapter.notifyDataSetChanged();

            cart_count.setText(String.valueOf(myApplication.getProductsArraylist().size()) + " Products - Rs. 475");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyDataAdapter<T> extends BaseAdapter {
        List<AddCart_model> AddCart_models;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<AddCart_model> AddCart_models, Activity activity) {
            this.AddCart_models =  AddCart_models;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return AddCart_models.size();
        }

        @Override
        public Object getItem(int position) {
            return AddCart_models.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyDataAdapter.ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new MyDataAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.card_layout, null);

                viewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

                viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.price);

                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (FancyButton) convertView.findViewById(R.id.plus);
                viewHolder.minus = (FancyButton) convertView.findViewById(R.id.minus);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyDataAdapter.ViewHolder) convertView.getTag();
            }

            AddCart_model product_pojo = AddCart_models.get(position);

            viewHolder.product_name.setText(product_pojo.getProductName());

            viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity,product_pojo.getProductImage()));
            viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));

            viewHolder.weight.setText(product_pojo.getUnit() + product_pojo.getUnitsOfMeasurement());
            viewHolder.price.setText("25rs");

            viewHolder.count.setText(String.valueOf(product_pojo.getQuantity()));

            viewHolder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        int t = Integer.parseInt(viewHolder.count.getText().toString());
                        viewHolder.count.setText(String.valueOf(t+1));
                    }
                    catch (Exception e)
                    {

                    }
                }
            });

            viewHolder.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int t = Integer.parseInt(viewHolder.count.getText().toString());
                        if(t > 0)
                        {
                            viewHolder.count.setText(String.valueOf(t-1));
                        }
                    }catch (Exception e)
                    {

                    }
                }
            });

            /*viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(activity, DetailsActivity.class);
                    startActivity(in);
                }
            });*/

            return convertView;
        }

        private class ViewHolder {
            TextView product_name,weight,price,count;
            SimpleDraweeView imageView;
            FancyButton plus,minus;
        }
    }

    public String loadProductJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("products_mock.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
