package com.kesari.trackingfresh.ProductSubFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.DetailPage.DetailsActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.network.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kesari on 11/04/17.
 */

public class Product_categoryFragment extends Fragment {
    GridView gridview;
    private MyDataAdapter myDataAdapter;
    private Gson gson;
    private SubProductMainPOJO subProductMainPOJO;
    MyApplication myApplication;
    private String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product_category, container, false);

        gridview = (GridView) V.findViewById(R.id.list);

        gson = new Gson();

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getProductData();

        myApplication = (MyApplication) getApplicationContext();
    }


    public void getProductData() {

        try {

            Bundle args = getArguments();

            String category_id = args.getString("category_id");

            Log.i("Subcategory_url", Constants.Product_Desc + category_id);

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), Constants.Product_Desc + category_id, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("product_category", result);

                    getProductDataResponse(result);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProductDataResponse(String Response) {

        try {

            subProductMainPOJO = gson.fromJson(Response, SubProductMainPOJO.class);

            myDataAdapter = new MyDataAdapter(subProductMainPOJO.getData(), getActivity());
            gridview.setAdapter(myDataAdapter);
            myDataAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyDataAdapter<T> extends BaseAdapter {
        List<SubProductSubPOJO> SubProductSubPOJOs;
        private Activity activity;
        private LayoutInflater layoutInflater = null;

        public MyDataAdapter(List<SubProductSubPOJO> SubProductSubPOJOs, Activity activity) {
            this.SubProductSubPOJOs = SubProductSubPOJOs;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return SubProductSubPOJOs.size();
        }

        @Override
        public Object getItem(int position) {
            return SubProductSubPOJOs.get(position);
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

            try {

                final SubProductSubPOJO product_pojo = SubProductSubPOJOs.get(position);

                viewHolder.product_name.setText(product_pojo.getProductName());

                viewHolder.imageView.setController(IOUtils.getFrescoImageController(activity, product_pojo.getProductImage()));
                viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(activity));

                viewHolder.weight.setText(product_pojo.getUnit() + product_pojo.getUnitsOfMeasurement());
                viewHolder.price.setText("100rs");

                viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.holder_count.setVisibility(View.VISIBLE);
                        viewHolder.addtoCart.setVisibility(View.GONE);
                        viewHolder.count.setText("1");


                        if (!myApplication.checkifproductexists(product_pojo.getProductId())) {
                            AddCart_model addCart_model = new AddCart_model();
                            addCart_model.setProductCategory(product_pojo.getProductCategory());
                            addCart_model.setProductId(product_pojo.getProductId());
                            addCart_model.setProductName(product_pojo.getProductName());
                            addCart_model.set_id(product_pojo.get_id());
                            addCart_model.setUnitsOfMeasurement(product_pojo.getUnitsOfMeasurement());
                            addCart_model.setProductCategoryId(product_pojo.getProductCategoryId());
                            addCart_model.setProductDescription(product_pojo.getProductDescription());
                            addCart_model.setProductDetails(product_pojo.getProductDetails());
                            addCart_model.setUnit(product_pojo.getUnit());
                            addCart_model.setUnitsOfMeasurementId(product_pojo.getUnitsOfMeasurementId());
                            addCart_model.setProductImage(product_pojo.getProductImage());
                            addCart_model.setActive(product_pojo.getActive());
                            addCart_model.setQuantity(1);

                            myApplication.setProducts(addCart_model);
                        } else {
                            //Toast.makeText(activity, "Product already present in cart!!", Toast.LENGTH_SHORT).show();
                            viewHolder.count.setText(myApplication.getProductQuantity(product_pojo.getProductId()));

                        }
                    }
                });

                viewHolder.plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int t = Integer.parseInt(viewHolder.count.getText().toString());
                            viewHolder.count.setText(String.valueOf(t + 1));

                            //DashboardActivity.updateNotificationsBadge(t + 1);

                            if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                            } else {

                            }

                        } catch (Exception e) {

                        }
                    }
                });

                viewHolder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int t = Integer.parseInt(viewHolder.count.getText().toString());
                            if (t > 0) {
                                viewHolder.count.setText(String.valueOf(t - 1));

                                //DashboardActivity.updateNotificationsBadge(t - 1);

                                if (!myApplication.DecrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            }

                            if (t == 1) {

                                myApplication.RemoveProductonZeroQuantity(product_pojo.getProductId());

                                viewHolder.holder_count.setVisibility(View.GONE);
                                viewHolder.addtoCart.setVisibility(View.VISIBLE);

                                viewHolder.count.setText("0");
                            }
                        } catch (Exception e) {

                        }
                    }
                });

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent in = new Intent(activity, DetailsActivity.class);
                            in.putExtra("_id", product_pojo.get_id());
                            in.putExtra("unitsOfMeasurement", product_pojo.getUnitsOfMeasurement());
                            in.putExtra("productCategory", product_pojo.getProductCategory());
                            in.putExtra("productCategoryId", product_pojo.getProductCategoryId());
                            in.putExtra("productName", product_pojo.getProductName());
                            in.putExtra("productDescription", product_pojo.getProductDescription());
                            in.putExtra("productDetails", product_pojo.getProductDetails());
                            in.putExtra("unit", product_pojo.getUnit());
                            in.putExtra("unitsOfMeasurementId", product_pojo.getUnitsOfMeasurementId());
                            in.putExtra("productId", product_pojo.getProductId());
                            in.putExtra("productImage", product_pojo.getProductImage());
                            in.putExtra("active", product_pojo.getActive());

                            //in.putExtra("quantity",String.valueOf(viewHolder.count.getText().toString().trim()));
                            startActivity(in);

                        } catch (Exception e) {
                            Log.i("Exception_MyDataAdapter", e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return convertView;
        }

        private class ViewHolder {
            TextView product_name, weight, price, count;
            SimpleDraweeView imageView;
            FancyButton plus, minus;
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
