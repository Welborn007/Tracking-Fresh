package com.trackingfresh.customer.DetailPage;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trackingfresh.customer.AddToCart.AddCart_model;
import com.trackingfresh.customer.ProductSubFragment.SubProductSubPOJO;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.network.MyApplication;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by SNK Consulting on 26-09-2017.
 */

public class OffersRecylerAdapter extends RecyclerView.Adapter<OffersRecylerAdapter.RecyclerViewHolder> {
    List<SubProductSubPOJO> ProductCategorySubPOJOs;
    Context context;
    MyApplication myApplication;
    int selected_position = -1;
    private String TAG = this.getClass().getSimpleName();

    public OffersRecylerAdapter(List<SubProductSubPOJO> ProductCategorySubPOJOs, Context context, MyApplication myApplication) {
        this.ProductCategorySubPOJOs = ProductCategorySubPOJOs;
        this.context = context;
        this.myApplication = myApplication;
    }

    @Override
    public OffersRecylerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.offer_layout, parent, false);
//        mainGroup.getLayoutParams().width = (int) ((getScreenWidth()) / 1.6f);

        OffersRecylerAdapter.RecyclerViewHolder recyclerViewHolder = new OffersRecylerAdapter.RecyclerViewHolder(mainGroup);

       /* View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
*/
        return recyclerViewHolder;
    }

    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public void onBindViewHolder(final OffersRecylerAdapter.RecyclerViewHolder viewHolder, final int position) {

        try {

/*
            if(selected_position == position){

                holder.product_name.setBackgroundColor(Color.parseColor("#80CBC4"));

            }else{

                holder.product_name.setBackgroundColor(Color.parseColor("#ffffff"));
            }
*/

          /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notifyItemChanged(selected_position);
                    selected_position = position;
                    notifyItemChanged(selected_position);

                    selected_position = position;
                    notifyDataSetChanged();
                }
            });
*/
    /*        holder.product_name.setText(ProductCategorySubPOJOs.get(position).getCategoryName().toString());

            holder.product_image.setController(IOUtils.getFrescoImageController(context,ProductCategorySubPOJOs.get(position).getCategoryImage()));
            holder.product_image.setHierarchy(IOUtils.getFrescoImageHierarchy(context));
*/

            try {

                final SubProductSubPOJO product_pojo = ProductCategorySubPOJOs.get(position);

                viewHolder.product_name.setText(product_pojo.getProductName());

                viewHolder.imageView.setController(IOUtils.getFrescoImageController(context, product_pojo.getProductImage()));
                viewHolder.imageView.setHierarchy(IOUtils.getFrescoImageHierarchy(context));
//                viewHolder.imageView.getHierarchy().setFailureImage(R.drawable.fruit);

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
                            try {
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

                                if (product_pojo.getProductImages().isEmpty()) {
                                    addCart_model.setProductImage(product_pojo.getProductImage());
                                } else {
                                    addCart_model.setProductImage(product_pojo.getProductImages().get(0).getUrl());
                                }

                                addCart_model.setActive(product_pojo.getActive());
                                addCart_model.setQuantity(1);
                                addCart_model.setBrand(product_pojo.getBrand());
                                addCart_model.setAvailableQuantity(product_pojo.getAvailableQuantity());
                                addCart_model.setMRP(product_pojo.getMRP());

                                if (product_pojo.getOffer() != null) {
                                    addCart_model.setOffer(product_pojo.getOffer());
                                } else {
                                    addCart_model.setOffer("false");
                                }

                                myApplication.setProducts(addCart_model);
                            } catch (Exception e) {
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

                            if (t < Integer.parseInt(product_pojo.getAvailableQuantity())) {
                                viewHolder.count.setVisibility(View.VISIBLE);
                                viewHolder.crossTextView.setVisibility(View.VISIBLE);
                                viewHolder.count.setText(String.valueOf(t + 1));
                              if (!myApplication.IncrementProductQuantity(product_pojo.getProductId())) {

                                } else {

                                }
                            } else {
                                final Dialog dialog = new Dialog(context);
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

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent in = new Intent(context, DetailsActivity.class);
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
                            if (product_pojo.getProductImages().isEmpty()) {
                                in.putExtra("productImage", product_pojo.getProductImage());
                                in.putExtra("productImages", "");
                            } else {
                                in.putExtra("productImage", product_pojo.getProductImages().get(0).getUrl());

                                //Set the values
                                Gson gson = new Gson();
                                String jsonText = gson.toJson(product_pojo.getProductImages());
                                in.putExtra("productImages", jsonText);
                            }
                            in.putExtra("active", product_pojo.getActive());
                            in.putExtra("price", product_pojo.getSelling_price());
                            in.putExtra("brand", product_pojo.getBrand());
                            in.putExtra("quantity", product_pojo.getAvailableQuantity());
                            in.putExtra("MRP", product_pojo.getMRP());

                            if (product_pojo.getOffer() != null) {
                                in.putExtra("Offer", product_pojo.getOffer());
                            } else {
                                in.putExtra("Offer", "false");
                            }

                            //in.putExtra("quantity",String.valueOf(viewHolder.count.getText().toString().trim()));
                            context.startActivity(in);

                        } catch (Exception e) {
                            Log.i("Exception_MyDataAdapter", e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    @Override
    public int getItemCount() {

        return ProductCategorySubPOJOs.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView product_name, weight, price, count, quantity, mrp;
        SimpleDraweeView imageView;
        ImageView plus, minus;
        ImageView addtoCart;
        LinearLayout holder_count;
        TextView crossTextView;

        public RecyclerViewHolder(View convertView) {
            super(convertView);
            product_name = (TextView) convertView.findViewById(R.id.product_name);
            imageView = (SimpleDraweeView) convertView.findViewById(R.id.images);

            weight = (TextView) convertView.findViewById(R.id.weight);
            price = (TextView) convertView.findViewById(R.id.price);
            quantity = (TextView) convertView.findViewById(R.id.quantity);
            count = (TextView) convertView.findViewById(R.id.count);
            plus = (ImageView) convertView.findViewById(R.id.plus);
            minus = (ImageView) convertView.findViewById(R.id.minus);
            mrp = (TextView) convertView.findViewById(R.id.mrp);
            addtoCart = (ImageView) convertView.findViewById(R.id.addtoCart);
            holder_count = (LinearLayout) convertView.findViewById(R.id.holder_count);
            crossTextView = (TextView) convertView.findViewById(R.id.crossTextView);

        }
    }

}
