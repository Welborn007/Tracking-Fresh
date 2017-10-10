package com.kesari.trackingfresh.YourOrders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.AddToCart.AddCart_model;
import com.kesari.trackingfresh.Cart.AddToCart;
import com.kesari.trackingfresh.Order.OrderReview;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.RecyclerItemClickListener;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;
import com.kesari.trackingfresh.YourOrders.RepeatOrder.RepeatOrderMainPojo;
import com.kesari.trackingfresh.network.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OrdersListRecycler_Adapter extends RecyclerView.Adapter<OrdersListRecycler_Adapter.RecyclerViewHolder> {

    private List<OrderSubPOJO> OrdersListReView;
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    String ReasonsValue = "";
    RecyclerView recyclerView;
    private CancelReasonMainPOJO cancelReasonMainPOJO;
    Gson gson;
    private CancelReasons_RecyclerAdapter cancelReasons_recyclerAdapter;
    String ReasonData = "";
    MyApplication myApplication;
    String productRemoved = "";

    private RepeatOrderMainPojo repeatOrderMainPojo;

    public OrdersListRecycler_Adapter(List<OrderSubPOJO> OrdersListReView, Context context) {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public OrdersListRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_rowlayout, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        myApplication = (MyApplication) getApplicationContext();

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersListRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getCreatedBy());
            holder.orderNo.setText(OrdersListReView.get(position).getOrderNo());

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdfInput.parse(OrdersListReView.get(position).getCreatedAt());
            String orderDateFormatted = sdfOutput.format(d);
            holder.orderDate.setText(orderDateFormatted);

            if (OrdersListReView.get(position).getPayment_Status() == null) {
                holder.payment_confirmHolder.setVisibility(View.GONE);
            } else {
                holder.payment_confirmHolder.setVisibility(View.VISIBLE);
                holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_Status());
            }

            if (OrdersListReView.get(position).getPayment_Mode() == null) {
                holder.payment_modeHolder.setVisibility(View.GONE);
            } else {
                holder.payment_modeHolder.setVisibility(View.VISIBLE);
                holder.payment_mode.setText(OrdersListReView.get(position).getPayment_Mode());
            }

            holder.total_price.setText("₹ " + OrdersListReView.get(position).getTotal_price());


            if (OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected")) {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.VISIBLE);
                holder.order_status.setImageResource(R.drawable.rejected);
                holder.cancel.setVisibility(View.GONE);

                if (OrdersListReView.get(position).getRejectReason() != null) {
                    if (!OrdersListReView.get(position).getRejectReason().isEmpty()) {
                        holder.rejectReason.setText(OrdersListReView.get(position).getRejectReason());
                        holder.rejectHolder.setVisibility(View.VISIBLE);
                    } else {
                        holder.rejectHolder.setVisibility(View.GONE);
                    }
                }
            } else if (OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted")) {
                holder.rejectHolder.setVisibility(View.GONE);
                holder.cancelHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.accepted);
                holder.cancel.setVisibility(View.VISIBLE);
            } else if (OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending")) {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.pending);
                holder.cancel.setVisibility(View.VISIBLE);
            } else if (OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled")) {
                holder.cancelHolder.setVisibility(View.VISIBLE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.cancel);
                holder.cancel.setVisibility(View.GONE);

                if (OrdersListReView.get(position).getCancelReason() != null) {
                    if (!OrdersListReView.get(position).getCancelReason().isEmpty()) {
                        holder.cancelReason.setText(OrdersListReView.get(position).getCancelReason());
                        holder.cancelHolder.setVisibility(View.VISIBLE);
                    } else {
                        holder.cancelHolder.setVisibility(View.GONE);
                    }
                }
            } else if (OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered")) {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.delivered);
                holder.cancel.setVisibility(View.GONE);
            }


            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fetchCancellationReasons(context, position);
                }
            });

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, OrderReview.class);
                    intent.putExtra("orderID", orderID);
                    context.startActivity(intent);
                }
            });

            if (SharedPrefUtil.getNearestRouteMainPOJO(context) != null) {
                String VehicleID = SharedPrefUtil.getNearestRouteMainPOJO(context).getData().get(0).getVehicleId();

                Log.i("VEhicleID", VehicleID);
                holder.repeat.setVisibility(View.VISIBLE);
            } else {
                Log.i("VEhicleID", "Not Present");

                holder.repeat.setVisibility(View.GONE);
            }

            if (OrdersListReView.get(position).getPickUp() != null) {
                if (OrdersListReView.get(position).getPickUp().equalsIgnoreCase("true")) {
                    holder.orderType.setText("Pick Up");
                } else {
                    holder.orderType.setText("Delivery");
                }
            } else {
                holder.orderType.setText("Delivery");
            }

            holder.repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myApplication.removeProductsItems();

                    Gson gson = new Gson();
                    String jsonText = gson.toJson(OrdersListReView.get(position).getOrders());
                    Log.i("DataFromOrder", jsonText);
                    RepeatOrders(jsonText);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return OrdersListReView.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView order_number, customer_name, payment_confirm, payment_mode, total_price, cancelReason, rejectReason, orderDate, orderNo, orderType;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder, payment_modeHolder, cancelHolder, rejectHolder;
        FancyButton cancel, repeat;

        public RecyclerViewHolder(View view) {
            super(view);
            order_number = (TextView) view.findViewById(R.id.order_number);
            customer_name = (TextView) view.findViewById(R.id.customer_name);
            payment_confirm = (TextView) view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView) view.findViewById(R.id.payment_mode);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);
            cancelReason = (TextView) view.findViewById(R.id.cancelReason);
            rejectReason = (TextView) view.findViewById(R.id.rejectReason);
            orderDate = (TextView) view.findViewById(R.id.orderDate);
            orderNo = (TextView) view.findViewById(R.id.orderNo);
            orderType = (TextView) view.findViewById(R.id.orderType);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);
            cancelHolder = (LinearLayout) view.findViewById(R.id.cancelHolder);
            rejectHolder = (LinearLayout) view.findViewById(R.id.rejectHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);
            cancel = (FancyButton) view.findViewById(R.id.cancel);
            repeat = (FancyButton) view.findViewById(R.id.repeat);
        }
    }

    private void RepeatOrders(String Products) {
        try {

            String url = Constants.RepeatOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();
                JSONArray produtsArray = new JSONArray(Products);
                postObject.put("products", produtsArray);

                postObject.put("vehicleId", SharedPrefUtil.getNearestRouteMainPOJO(context).getData().get(0).getVehicleId());

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i(TAG, result);

                    RepeatOrderResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void RepeatOrderResponse(String Response) {
        try {
            gson = new Gson();
            repeatOrderMainPojo = gson.fromJson(Response, RepeatOrderMainPojo.class);

            if (repeatOrderMainPojo.getData() != null) {
                if (!repeatOrderMainPojo.getData().getProducts().isEmpty()) {
                    try {

                        JSONObject jsonObjectMain = new JSONObject(Response);
                        JSONObject jsonObjectData = jsonObjectMain.getJSONObject("data");

                        JSONArray jsonArray = jsonObjectData.getJSONArray("products");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String productId, productName, quantity, price, active, productCategory, _id, unitsOfMeasurement, productCategoryId, productDescription,
                                    productDetails, unit, unitsOfMeasurementId, productImage, brand, availableQuantity, MRP, selling_price, sub_total;

                            if (jsonObject.has("productId")) {
                                productId = jsonObject.getString("productId");
                            } else {
                                productId = "";
                            }

                            if (jsonObject.has("productName")) {
                                productName = jsonObject.getString("productName");
                            } else {
                                productName = "";
                            }

                            if (jsonObject.has("quantity")) {
                                quantity = jsonObject.getString("quantity");
                            } else {
                                quantity = "0";
                            }

                            if (jsonObject.has("price")) {
                                price = jsonObject.getString("price");
                            } else {
                                price = "";
                            }

                            if (jsonObject.has("active")) {
                                active = jsonObject.getString("active");
                            } else {
                                active = "";
                            }

                            if (jsonObject.has("productCategory")) {
                                productCategory = jsonObject.getString("productCategory");
                            } else {
                                productCategory = "";
                            }

                            if (jsonObject.has("_id")) {
                                _id = jsonObject.getString("_id");
                            } else {
                                _id = "";
                            }

                            if (jsonObject.has("unitsOfMeasurement")) {
                                unitsOfMeasurement = jsonObject.getString("unitsOfMeasurement");
                            } else {
                                unitsOfMeasurement = "";
                            }

                            if (jsonObject.has("productCategoryId")) {
                                productCategoryId = jsonObject.getString("productCategoryId");
                            } else {
                                productCategoryId = "";
                            }

                            if (jsonObject.has("productDescription")) {
                                productDescription = jsonObject.getString("productDescription");
                            } else {
                                productDescription = "";
                            }

                            if (jsonObject.has("productDetails")) {
                                productDetails = jsonObject.getString("productDetails");
                            } else {
                                productDetails = "";
                            }

                            if (jsonObject.has("unit")) {
                                unit = jsonObject.getString("unit");
                            } else {
                                unit = "";
                            }

                            if (jsonObject.has("unitsOfMeasurementId")) {
                                unitsOfMeasurementId = jsonObject.getString("unitsOfMeasurementId");
                            } else {
                                unitsOfMeasurementId = "";
                            }

                            if (jsonObject.has("productImage")) {
                                productImage = jsonObject.getString("productImage");
                            } else {
                                productImage = "";
                            }

                            if (jsonObject.has("brand")) {
                                brand = jsonObject.getString("brand");
                            } else {
                                brand = "";
                            }

                            if (jsonObject.has("availableQuantity")) {
                                availableQuantity = jsonObject.getString("availableQuantity");
                            } else {
                                availableQuantity = "0";
                            }

                            if (jsonObject.has("MRP")) {
                                MRP = jsonObject.getString("MRP");
                            } else {
                                MRP = "";
                            }

                            if (jsonObject.has("sub_total")) {
                                sub_total = jsonObject.getString("sub_total");
                            } else {
                                sub_total = "";
                            }

                            if (jsonObject.has("selling_price")) {
                                selling_price = jsonObject.getString("selling_price");
                            } else {
                                selling_price = "";
                            }


                                AddCart_model addCart_model = new AddCart_model();
                                addCart_model.setProductId(productId);
                                addCart_model.setProductName(productName);
                                addCart_model.setPrice(selling_price);
                                addCart_model.setActive(active);
                                addCart_model.setProductCategory(productCategory);
                                addCart_model.set_id(_id);
                                addCart_model.setUnitsOfMeasurement(unitsOfMeasurement);
                                addCart_model.setUnitsOfMeasurementId(unitsOfMeasurementId);
                                addCart_model.setProductCategoryId(productCategoryId);
                                addCart_model.setProductDescription(productDescription);
                                addCart_model.setProductDetails(productDetails);
                                addCart_model.setUnit(unit);
                                addCart_model.setProductImage(productImage);
                                addCart_model.setBrand(brand);
                                addCart_model.setMRP(MRP);

                                if (Integer.parseInt(quantity) > Integer.parseInt(availableQuantity)) {
                                    addCart_model.setQuantity(Integer.parseInt(availableQuantity));
                                    addCart_model.setAvailableQuantity(availableQuantity);

                                } else {
                                    addCart_model.setQuantity(Integer.parseInt(quantity));
                                    addCart_model.setAvailableQuantity(availableQuantity);
                                }


                                myApplication.setProducts(addCart_model);
                        }

                        if(repeatOrderMainPojo.getData().getIsDeleted().equalsIgnoreCase("true"))
                        {
                            productRemoved = "true";
                        }

                        Intent intent = new Intent(context, AddToCart.class);
                        intent.putExtra("productRemoved", productRemoved);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Sorry...")
                            .setContentText("Currently no products for the order are available in Vehicle!")
                            .show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCancellationReasons(final Context context, final int pos) {
        try {

            String url = Constants.Reasons + "cancel";

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    CancelReasonsResponse(result, pos);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void CancelReasonsResponse(String Reasons, final int position) {
        try {
            gson = new Gson();
            cancelReasonMainPOJO = gson.fromJson(Reasons, CancelReasonMainPOJO.class);

            // Create custom dialog object
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.setContentView(R.layout.cancellation_reasons);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            FancyButton cancel = (FancyButton) dialog.findViewById(R.id.cancel);
            final EditText editText = (EditText) dialog.findViewById(R.id.other);

            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            ReasonData = cancelReasonMainPOJO.getData().get(position).getReason();

                            if (ReasonData.equalsIgnoreCase("Other")) {
                                editText.setVisibility(View.VISIBLE);
                            } else {
                                editText.setVisibility(View.GONE);
                                ReasonsValue = cancelReasonMainPOJO.getData().get(position).getReason();
                            }
                        }
                    })
            );

            cancelReasons_recyclerAdapter = new CancelReasons_RecyclerAdapter(cancelReasonMainPOJO.getData(), context);
            recyclerView.setAdapter(cancelReasons_recyclerAdapter);
            cancelReasons_recyclerAdapter.notifyDataSetChanged();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ReasonData.equalsIgnoreCase("Other")) {
                        ReasonsValue = editText.getText().toString().trim();
                    }

                    if (!ReasonsValue.isEmpty()) {
                        updateOrderDetails(OrdersListReView.get(position).get_id(), "Cancelled", ReasonsValue);
                        //Toast.makeText(context, ReasonsValue, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        //Toast.makeText(context, "Please mention reason!!!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(context)
                                .setTitleText("Please mention reason!!!")
                                .show();
                    }
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();

        } catch (Exception w) {
            w.printStackTrace();
        }
    }

    private void updateOrderDetails(String orderID, String OrderStatus, String Remarks) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status", OrderStatus);
                postObject.put("cancelReason", Remarks);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.sendJSONObjectPutRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    UpdateResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void UpdateResponse(String Response) {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if (message.equalsIgnoreCase("Updated Successfull!!")) {
                OrderListActivity.getOrderList(context);
                ReasonsValue = "";
                ReasonData = "";
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
