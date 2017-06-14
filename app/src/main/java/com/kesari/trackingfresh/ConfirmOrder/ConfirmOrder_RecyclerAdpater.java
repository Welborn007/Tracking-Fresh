package com.kesari.trackingfresh.ConfirmOrder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesari.trackingfresh.R;

import java.util.List;

/**
 * Created by kesari on 08/06/17.
 */

public class ConfirmOrder_RecyclerAdpater extends RecyclerView.Adapter<ConfirmOrder_RecyclerAdpater.RecyclerViewHolder>
{
    List<OrderAddListPOJO> OrderAddListPOJOs;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public ConfirmOrder_RecyclerAdpater(List<OrderAddListPOJO> OrderAddListPOJOs, Context context)
    {
        this.OrderAddListPOJOs = OrderAddListPOJOs;
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

            holder.product_name.setText(OrderAddListPOJOs.get(position).getProductName());
            holder.quantity.setText(OrderAddListPOJOs.get(position).getQuantity());
            holder.price.setText(OrderAddListPOJOs.get(position).getPrice());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    @Override
    public int getItemCount() {

        return OrderAddListPOJOs.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView product_name,quantity,price;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            price = (TextView) view.findViewById(R.id.price);

        }
    }
}