package com.trackingfresh.customer.TKCash;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackingfresh.customer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kesari on 07/07/17.
 */

public class WalletRecycler_Adapter extends RecyclerView.Adapter<WalletRecycler_Adapter.RecyclerViewHolder>{

    private List<WalletSubPOJO> OrdersListReView;
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    String Value;

    public WalletRecycler_Adapter(List<WalletSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public WalletRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_rowlayout,parent,false);

        WalletRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new WalletRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(WalletRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-LLL-yyyy hh:mm");

            if (!OrdersListReView.get(position).getCreatedAt().isEmpty()){

                try {

                    holder.date_time.setVisibility(View.VISIBLE);
                    Date d = sdfInput.parse(OrdersListReView.get(position).getCreatedAt());
                    String formatteddob = sdfOutput.format(d);
                    holder.date_time.setText(formatteddob);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else {

                holder.date_time.setVisibility(View.GONE);
            }
            
            
            holder.paidfor.setText(OrdersListReView.get(position).getSource());
            holder.amount.setText(OrdersListReView.get(position).getSourceAmount() + " ₹");
            holder.sign.setText(OrdersListReView.get(position).getOperation());

            if(OrdersListReView.get(position).getOperation().equalsIgnoreCase("-"))
            {
                holder.wallet_icon.setImageResource(R.drawable.ic_wallet_minus);
            }
            else if(OrdersListReView.get(position).getOperation().equalsIgnoreCase("+"))
            {
                holder.wallet_icon.setImageResource(R.drawable.ic_wallet_add);
            }
            else
            {
                holder.wallet_icon.setImageResource(R.drawable.ic_wallet_simple);
            }
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
        TextView paidfor,date_time,sign,amount;
        ImageView wallet_icon;
        CardView subItemCard_view;


        public RecyclerViewHolder(View view)
        {
            super(view);
            paidfor = (TextView)view.findViewById(R.id.paidfor);
            date_time = (TextView)view.findViewById(R.id.date_time);
            sign = (TextView)view.findViewById(R.id.sign);
            amount = (TextView)view.findViewById(R.id.amount);

            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            wallet_icon = (ImageView) view.findViewById(R.id.wallet_icon);
        }
    }
}
