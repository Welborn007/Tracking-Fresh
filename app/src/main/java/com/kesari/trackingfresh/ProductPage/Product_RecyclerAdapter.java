package com.kesari.trackingfresh.ProductPage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.IOUtils;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 08/05/17.
 */

public class Product_RecyclerAdapter extends RecyclerView.Adapter<Product_RecyclerAdapter.RecyclerViewHolder>
{
    List<Product_POJO>Product_POJOs;
    Context context;
    int selected_position = -1;

    public Product_RecyclerAdapter(List<Product_POJO> Product_POJOs, Context context)
    {
        this.Product_POJOs = Product_POJOs;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        if(selected_position == position){

            holder.product_name.setBackgroundColor(Color.parseColor("#80CBC4"));

        }else{

            holder.product_name.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                selected_position = position;
                notifyDataSetChanged();
            }
        });

        holder.product_name.setText(Product_POJOs.get(position).getProduct_name().toString());

        holder.product_image.setController(IOUtils.getFrescoImageController(context,Product_POJOs.get(position).getImages()));
        holder.product_image.setHierarchy(IOUtils.getFrescoImageHierarchy(context));
    }

    @Override
    public int getItemCount() {

        return Product_POJOs.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        FancyButton product_name;
        SimpleDraweeView product_image;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (FancyButton) view.findViewById(R.id.product_name);
            product_image = (SimpleDraweeView) view.findViewById(R.id.product_image);
        }
    }
}