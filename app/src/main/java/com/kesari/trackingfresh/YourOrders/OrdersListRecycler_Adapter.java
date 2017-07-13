package com.kesari.trackingfresh.YourOrders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kesari.trackingfresh.Order.OrderReview;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OrdersListRecycler_Adapter extends RecyclerView.Adapter<OrdersListRecycler_Adapter.RecyclerViewHolder>{

    private List<OrderSubPOJO> OrdersListReView;
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    String Value;

    public OrdersListRecycler_Adapter(List<OrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public OrdersListRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersListRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getCreatedBy());


            if(OrdersListReView.get(position).getPayment_Status() == null)
            {
                holder.payment_confirmHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_confirmHolder.setVisibility(View.VISIBLE);
                holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_Status());
            }

            if(OrdersListReView.get(position).getPayment_Mode() == null)
            {
                holder.payment_modeHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_modeHolder.setVisibility(View.VISIBLE);
                holder.payment_mode.setText(OrdersListReView.get(position).getPayment_Mode());
            }

            holder.total_price.setText(OrdersListReView.get(position).getTotal_price());

            if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.order_status.setImageResource(R.drawable.rejected);
                holder.cancel.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.order_status.setImageResource(R.drawable.accepted);
                holder.cancel.setVisibility(View.VISIBLE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.order_status.setImageResource(R.drawable.pending);
                holder.cancel.setVisibility(View.VISIBLE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.order_status.setImageResource(R.drawable.cancel);
                holder.cancel.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.order_status.setImageResource(R.drawable.delivered);
                holder.cancel.setVisibility(View.GONE);
            }


            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //updateOrderDetails(OrdersListReView.get(position).get_id(),"Cancelled");

                    SelectReasonForCancellation(position);
                }
            });

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, OrderReview.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return OrdersListReView.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView order_number,customer_name,payment_confirm,payment_mode,time_txt,distance_txt,total_price;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder,payment_modeHolder;
        Button cancel;

        public RecyclerViewHolder(View view)
        {
            super(view);
            order_number = (TextView)view.findViewById(R.id.order_number);
            customer_name = (TextView)view.findViewById(R.id.customer_name);
            payment_confirm = (TextView)view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView)view.findViewById(R.id.payment_mode);
            distance_txt = (TextView) view.findViewById(R.id.distance_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);
            cancel = (Button) view.findViewById(R.id.cancel);
        }
    }

    private void SelectReasonForCancellation(final int position)
    {
        try {

            // Create custom dialog object
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.setContentView(R.layout.cancellation_reasons);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            final RadioGroup reasonGroup = (RadioGroup) dialog.findViewById(R.id.reasonGroup);

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            final EditText editText = (EditText) dialog.findViewById(R.id.other);

            reasonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    int selectedId= reasonGroup.getCheckedRadioButtonId();
                    RadioButton radioButton =(RadioButton) dialog.findViewById(selectedId);

                    Value = radioButton.getText().toString();

                    /*if(radioButton.getText().toString().equalsIgnoreCase("Duplicate order"))
                    {

                    }
                    else if(radioButton.getText().toString().equalsIgnoreCase("Incorrect order"))
                    {

                    }
                    else if(radioButton.getText().toString().equalsIgnoreCase("Change in Quantity"))
                    {

                    }
                    else if(radioButton.getText().toString().equalsIgnoreCase("Defective Products"))
                    {

                    }
                    else if(radioButton.getText().toString().equalsIgnoreCase("Late Delivery"))
                    {

                    }
                    else */
                    if(radioButton.getText().toString().equalsIgnoreCase("Other"))
                    {
                        editText.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        editText.setVisibility(View.GONE);
                    }

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!Value.isEmpty())
                    {
                        updateOrderDetails(OrdersListReView.get(position).get_id(),"Cancelled");
                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(context, "Select Reason!!!", Toast.LENGTH_SHORT).show();
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

        }catch (Exception e)
        {
            Log.i(TAG,"dialog_Mobile");
        }

    }

    private void updateOrderDetails(String orderID, String OrderStatus) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);

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
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void UpdateResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                OrderListActivity.getOrderList(context);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
