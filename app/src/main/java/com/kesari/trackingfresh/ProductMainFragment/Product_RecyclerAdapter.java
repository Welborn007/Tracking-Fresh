package com.kesari.trackingfresh.ProductMainFragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    List<ProductCategorySubPOJO>ProductCategorySubPOJOs;
    Context context;
    int selected_position = -1;
    private String TAG = this.getClass().getSimpleName();

    public Product_RecyclerAdapter(List<ProductCategorySubPOJO> ProductCategorySubPOJOs, Context context)
    {
        this.ProductCategorySubPOJOs = ProductCategorySubPOJOs;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.product_rowlayout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(mainGroup);

       /* View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
*/
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        try
        {

/*
            if(selected_position == position){

                holder.product_name.setBackgroundColor(Color.parseColor("#80CBC4"));

            }else{

                holder.product_name.setBackgroundColor(Color.parseColor("#ffffff"));
            }
*/

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

            holder.product_name.setText(ProductCategorySubPOJOs.get(position).getCategoryName().toString());

            holder.product_image.setController(IOUtils.getFrescoImageController(context,ProductCategorySubPOJOs.get(position).getCategoryImage()));
            holder.product_image.setHierarchy(IOUtils.getFrescoImageHierarchy(context));

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    @Override
    public int getItemCount() {

        return ProductCategorySubPOJOs.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView product_name;
        SimpleDraweeView product_image;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_image = (SimpleDraweeView) view.findViewById(R.id.product_image);
        }
    }
}