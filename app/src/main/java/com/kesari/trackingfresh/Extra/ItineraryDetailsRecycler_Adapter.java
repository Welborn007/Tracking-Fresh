package com.kesari.trackingfresh.Extra;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesari.trackingfresh.ProductPage.Product_POJO;
import com.kesari.trackingfresh.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 15/2/17.
 */

public class ItineraryDetailsRecycler_Adapter extends RecyclerView.Adapter<ItineraryDetailsRecycler_Adapter.RecyclerViewHolder>
{
    private List<Product_POJO> Product_POJOList;
    private Context ctx;

    public ItineraryDetailsRecycler_Adapter(List<Product_POJO> Product_POJO, Context ctx)
    {
        this.Product_POJOList = new ArrayList<>(Product_POJO);
        this.ctx = ctx;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.product_name.setText(Product_POJOList.get(position).getProduct_name());

        Picasso.with(ctx).load(Product_POJOList.get(position).getImages()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        //return noOfValue;
        return Product_POJOList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView product_name;
        ImageView imageView;

        public RecyclerViewHolder(View view) {
            super(view);
            product_name = (TextView)view.findViewById(R.id.product_name);
            imageView = (ImageView) view.findViewById(R.id.images);
        }

    }

}
