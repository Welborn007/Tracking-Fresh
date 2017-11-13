package com.trackingfresh.customer.Order;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.Utilities.IOUtils;

import java.util.List;

/**
 * Created by kesari on 13/06/17.
 */

public class OrderReViewRecyclerAdapter extends RecyclerView.Adapter<OrderReViewRecyclerAdapter.RecyclerViewHolder>{

    private List<OrderReviewProductPOJO> OrdersListReView;
    Context context;

    public OrderReViewRecyclerAdapter(List<OrderReviewProductPOJO> OrdersListReView, Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public OrderReViewRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_order_review_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderReViewRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.product_name.setText(OrdersListReView.get(position).getProductName());
            holder.quantity.setText(OrdersListReView.get(position).getQuantity()+" quantity");
            holder.price.setText("₹ " + OrdersListReView.get(position).getPrice());
            holder.subtotal.setText("₹ " + OrdersListReView.get(position).getSub_total());

            holder.images.setController(IOUtils.getFrescoImageController(context,OrdersListReView.get(position).getProductImage()));
            holder.images.setHierarchy(IOUtils.getFrescoImageHierarchy(context));

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
        TextView product_name,quantity,price,subtotal;
        SimpleDraweeView images;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView)view.findViewById(R.id.product_name);
            quantity = (TextView)view.findViewById(R.id.quantity);
            price = (TextView)view.findViewById(R.id.price);
            subtotal = (TextView) view.findViewById(R.id.subtotal);

            images = (SimpleDraweeView) view.findViewById(R.id.images);
        }
    }


}
