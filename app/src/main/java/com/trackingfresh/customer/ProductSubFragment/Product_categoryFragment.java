package com.trackingfresh.customer.ProductSubFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trackingfresh.customer.AddToCart.AddCart_model;
import com.trackingfresh.customer.DetailPage.DetailsActivity;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Utilities.Constants;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;
import com.trackingfresh.customer.network.MyApplication;

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
    ListView listView;
    private MyDataAdapter myDataAdapter;
    private Gson gson;
    private SubProductMainPOJO subProductMainPOJO;
    MyApplication myApplication;
    private String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_product_category, container, false);

        listView = (ListView) V.findViewById(R.id.list);
/*
        listView = (GridView) V.findViewById(R.id.list);
*/

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

            Log.i("Subcategory_url", Constants.Product_Desc + "?categoryId=" + category_id + "&vehicleId=" + SharedPrefUtil.getNearestRouteMainPOJO(getActivity()).getData().get(0).getVehicleId());

            String URL = Constants.Product_Desc + "?categoryId=" + category_id + "&vehicleId=" + SharedPrefUtil.getNearestRouteMainPOJO(getActivity()).getData().get(0).getVehicleId();

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), URL, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("product_category", result);

                    getProductDataResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

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
            listView.setAdapter(myDataAdapter);
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
                viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
                viewHolder.plus = (ImageView) convertView.findViewById(R.id.plus);
                viewHolder.minus = (ImageView) convertView.findViewById(R.id.minus);
                viewHolder.mrp = (TextView) convertView.findViewById(R.id.mrp);
                viewHolder.crossTextView = (TextView) convertView.findViewById(R.id.crossTextView);

                viewHolder.addtoCart = (ImageView) convertView.findViewById(R.id.addtoCart);
/*
                viewHolder.addtoCart = (FancyButton) convertView.findViewById(R.id.addtoCart);
*/
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
                viewHolder.quantity.setText(product_pojo.getAvailableQuantity() + " quantity");
                viewHolder.weight.setText(product_pojo.getUnit() + product_pojo.getUnitsOfMeasurement());
                viewHolder.price.setText("₹ " + product_pojo.getSelling_price());

                viewHolder.mrp.setPaintFlags(viewHolder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mrp.setText("₹ " + product_pojo.getMRP());

                viewHolder.addtoCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.holder_count.setVisibility(View.VISIBLE);
                        viewHolder.crossTextView.setVisibility(View.VISIBLE);
                        viewHolder.count.setVisibility(View.VISIBLE);
                        viewHolder.addtoCart.setVisibility(View.GONE);
                        viewHolder.count.setText("1");


                        if (!myApplication.checkifproductexists(product_pojo.getProductId())) {
                            try
                            {
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
                                addCart_model.setPrice(product_pojo.getSelling_price());
                                addCart_model.setUnitsOfMeasurementId(product_pojo.getUnitsOfMeasurementId());

                                if(product_pojo.getProductImages().isEmpty())
                                {
                                    addCart_model.setProductImage(product_pojo.getProductImage());
                                }
                                else
                                {
                                    addCart_model.setProductImage(product_pojo.getProductImages().get(0).getUrl());
                                }

                                addCart_model.setActive(product_pojo.getActive());
                                addCart_model.setQuantity(1);
                                addCart_model.setBrand(product_pojo.getBrand());
                                addCart_model.setAvailableQuantity(product_pojo.getAvailableQuantity());
                                addCart_model.setMRP(product_pojo.getMRP());

                                if(product_pojo.getOffer() != null)
                                {
                                    addCart_model.setOffer(product_pojo.getOffer());
                                }
                                else
                                {
                                    addCart_model.setOffer("false");
                                }

                                if(product_pojo.getMfgDate() != null)
                                {
                                    addCart_model.setMfgDate(product_pojo.getMfgDate());
                                }
                                else
                                {
                                    addCart_model.setMfgDate("");
                                }

                                if(product_pojo.getExpDate() != null)
                                {
                                    addCart_model.setExpDate(product_pojo.getExpDate());
                                }
                                else
                                {
                                    addCart_model.setExpDate("");
                                }

                                if(product_pojo.getQc() != null)
                                {
                                    addCart_model.setQc(product_pojo.getQc());
                                }
                                else
                                {
                                    addCart_model.setQc("");
                                }

                                if(product_pojo.getBatchNo() != null)
                                {
                                    addCart_model.setBatchNo(product_pojo.getBatchNo());
                                }
                                else
                                {
                                    addCart_model.setBatchNo("");
                                }

                                myApplication.setProducts(addCart_model);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }

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


                            //DashboardActivity.updateNotificationsBadge(t + 1);

                            if(t < Integer.parseInt(product_pojo.getAvailableQuantity()))
                            {
                                viewHolder.count.setText(String.valueOf(t + 1));

                                viewHolder.count.setVisibility(View.VISIBLE);
                                viewHolder.crossTextView.setVisibility(View.VISIBLE);
                                if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            }
                            else
                            {
                                final Dialog dialog = new Dialog(activity);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_unavailable_dialog);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.show();

                                FancyButton btnCancel = (FancyButton) dialog.findViewById(R.id.btnCancel);
                                btnCancel.setText("Oops! Only " + product_pojo.getAvailableQuantity() + " items available!!");
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                //Toast.makeText(activity, "Sorry quantity not available in Vehicle!!!", Toast.LENGTH_SHORT).show();
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
                                viewHolder.count.setVisibility(View.GONE);
                                viewHolder.crossTextView.setVisibility(View.GONE);
                                viewHolder.addtoCart.setVisibility(View.VISIBLE);

                                viewHolder.count.setText("0");
                            }
                        } catch (Exception e) {

                        }
                    }
                });

                convertView.setOnClickListener(new View.OnClickListener() {
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
                            if(product_pojo.getProductImages().isEmpty())
                            {
                                in.putExtra("productImage", product_pojo.getProductImage());
                                in.putExtra("productImages","");
                            }
                            else
                            {
                                in.putExtra("productImage", product_pojo.getProductImages().get(0).getUrl());

                                //Set the values
                                Gson gson = new Gson();
                                String jsonText = gson.toJson(product_pojo.getProductImages());
                                in.putExtra("productImages",jsonText);
                            }
                            in.putExtra("active", product_pojo.getActive());
                            in.putExtra("price",product_pojo.getSelling_price());
                            in.putExtra("brand",product_pojo.getBrand());
                            in.putExtra("quantity",product_pojo.getAvailableQuantity());
                            in.putExtra("MRP",product_pojo.getMRP());

                            if(product_pojo.getOffer() != null)
                            {
                                in.putExtra("Offer",product_pojo.getOffer());
                            }
                            else
                            {
                                in.putExtra("Offer","false");
                            }

                            if(product_pojo.getMfgDate() != null)
                            {
                                in.putExtra("mfgDate",product_pojo.getMfgDate());
                            }
                            else
                            {
                                in.putExtra("mfgDate","");
                            }

                            if(product_pojo.getExpDate() != null)
                            {
                                in.putExtra("expDate",product_pojo.getExpDate());
                            }
                            else
                            {
                                in.putExtra("expDate","");
                            }

                            if(product_pojo.getQc() != null)
                            {
                                in.putExtra("qc",product_pojo.getQc());
                            }
                            else
                            {
                                in.putExtra("qc","");
                            }

                            if(product_pojo.getBatchNo() != null)
                            {
                                in.putExtra("batchNo",product_pojo.getBatchNo());
                            }
                            else
                            {
                                in.putExtra("batchNo","");
                            }

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
            TextView product_name, weight, price, count,quantity,mrp,crossTextView;
            SimpleDraweeView imageView;
            ImageView plus, minus;
            ImageView addtoCart;
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
