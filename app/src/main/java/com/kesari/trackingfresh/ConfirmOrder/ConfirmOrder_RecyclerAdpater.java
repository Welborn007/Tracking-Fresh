package com.kesari.trackingfresh.ConfirmOrder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.trackingfresh.DeliveryAddress.OrderFareListPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;

import java.util.List;

/**
 * Created by kesari on 08/06/17.
 */

public class ConfirmOrder_RecyclerAdpater extends RecyclerView.Adapter<ConfirmOrder_RecyclerAdpater.RecyclerViewHolder>
{
    List<OrderFareListPOJO> OrderFareListPOJOs;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public ConfirmOrder_RecyclerAdpater(List<OrderFareListPOJO> OrderFareListPOJOs, Context context)
    {
        this.OrderFareListPOJOs = OrderFareListPOJOs;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_order_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        try
        {

            holder.product_name.setText(OrderFareListPOJOs.get(position).getProductName());
            holder.quantity.setText(OrderFareListPOJOs.get(position).getQuantity()+" quantity");
            holder.price.setText("₹ " + OrderFareListPOJOs.get(position).getPrice());
            holder.subtotal.setText("₹ " + OrderFareListPOJOs.get(position).getSub_total());

            holder.images.setController(IOUtils.getFrescoImageController(context,OrderFareListPOJOs.get(position).getProductImage()));
            holder.images.setHierarchy(IOUtils.getFrescoImageHierarchy(context));

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    @Override
    public int getItemCount() {

        return OrderFareListPOJOs.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView product_name,quantity,price,subtotal;
        SimpleDraweeView images;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            price = (TextView) view.findViewById(R.id.price);
            subtotal = (TextView) view.findViewById(R.id.subtotal);

            images = (SimpleDraweeView) view.findViewById(R.id.images);
        }
    }
}