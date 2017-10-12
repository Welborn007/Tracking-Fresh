package com.kesari.trackingfresh.YourOrders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesari.trackingfresh.R;

import java.util.List;

/**
 * Created by kesari on 26/07/17.
 */

public class CancelReasons_RecyclerAdapter extends RecyclerView.Adapter<CancelReasons_RecyclerAdapter.RecyclerViewHolder>{

    private List<CancelReasonDataPOJO> OrdersListReView;
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    int selected_position = -1;

    public CancelReasons_RecyclerAdapter(List<CancelReasonDataPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public CancelReasons_RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_reason_rwolayout,parent,false);

        CancelReasons_RecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new CancelReasons_RecyclerAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(CancelReasons_RecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

           holder.reasons.setText(OrdersListReView.get(position).getReason());

//            if(selected_position == position){
//
//                ((CardView)holder.itemView).setCardBackgroundColor(Color.parseColor("#ffffff"));
//
//            }else {
//                ((CardView) holder.itemView).setCardBackgroundColor(Color.parseColor("#9e9e9e"));
//            }

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
        TextView reasons;
        CardView subItemCard_view;

        public RecyclerViewHolder(View view)
        {
            super(view);
            reasons = (TextView)view.findViewById(R.id.reasons);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
        }
    }
}
