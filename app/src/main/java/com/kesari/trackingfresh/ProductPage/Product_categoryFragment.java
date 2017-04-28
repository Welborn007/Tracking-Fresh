package com.kesari.trackingfresh.ProductPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.trackingfresh.DetailPage.DetailsActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.network.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 11/04/17.
 */

public class Product_categoryFragment extends Fragment
{
    GridView gridview;
    private MyDataAdapter myDataAdapter;
    List<Product_POJO> product_pojos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product_category, container, false);

        gridview = (GridView) V.findViewById(R.id.list);

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getProductData();
    }


    public void getProductData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadProductJSONFromAsset());


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

            myDataAdapter = new MyDataAdapter(product_pojos,getActivity());
            gridview.setAdapter(myDataAdapter);
            myDataAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyDataAdapter<T> extends BaseAdapter {
        List<Product_POJO> Product_POJOs;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<Product_POJO> Product_POJOs, Activity activity) {
            this.Product_POJOs =  Product_POJOs;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return Product_POJOs.size();
        }

        @Override
        public Object getItem(int position) {
            return Product_POJOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.product_layout, null);

                viewHolder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

                viewHolder.weight = (TextView) convertView.findViewById(R.id.weight);
                viewHolder.price = (TextView) convertView.findViewById(R.id.price);

                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (FancyButton) convertView.findViewById(R.id.plus);
                viewHolder.minus = (FancyButton) convertView.findViewById(R.id.minus);

                viewHolder.addtoCart = (Button) convertView.findViewById(R.id.addtoCart);
                viewHolder.holder_count = (LinearLayout) convertView.findViewById(R.id.holder_count);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Product_POJO product_pojo = product_pojos.get(position);

            viewHolder.product_name.setText(product_pojo.getProduct_name());

            viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity,product_pojo.getImages()));
            viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));

            viewHolder.weight.setText(product_pojo.getKilo());
            viewHolder.price.setText(product_pojo.getRs());

            viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.holder_count.setVisibility(View.VISIBLE);
                    viewHolder.addtoCart.setVisibility(View.GONE);
                }
            });

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

                        if(t < 0 || t == 0)
                        {
                            viewHolder.holder_count.setVisibility(View.GONE);
                            viewHolder.addtoCart.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e)
                    {

                    }
                }
            });

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(activity, DetailsActivity.class);
                    startActivity(in);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView product_name,weight,price,count;
            SimpleDraweeView imageView;
            FancyButton plus,minus;
            Button addtoCart;
            LinearLayout holder_count;
        }
    }

    public String loadProductJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("products_mock.json");
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


}
