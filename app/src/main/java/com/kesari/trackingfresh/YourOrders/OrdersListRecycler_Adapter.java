package com.kesari.trackingfresh.YourOrders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kesari.trackingfresh.Map.JSON_POJO;
import com.kesari.trackingfresh.R;

import java.util.List;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OrdersListRecycler_Adapter extends RecyclerView.Adapter<OrdersListRecycler_Adapter.RecyclerViewHolder>{

    private List<JSON_POJO> OrdersListReView;


    public OrdersListRecycler_Adapter(List<JSON_POJO> OrdersListReView)
    {
        this.OrdersListReView = OrdersListReView;
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

            holder.order_number.setText(OrdersListReView.get(position).getId());
            holder.customer_name.setText(OrdersListReView.get(position).getCustomer_name());
            holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_confirmation());
            holder.payment_mode.setText(OrdersListReView.get(position).getPayment_mode());

            if(OrdersListReView.get(position).getOrder_status().equalsIgnoreCase("rejected"))
            {
                holder.order_status.setImageResource(R.drawable.rejected);
            }
            else if(OrdersListReView.get(position).getOrder_status().equalsIgnoreCase("accepted"))
            {
                holder.order_status.setImageResource(R.drawable.accepted);
            }
            else if(OrdersListReView.get(position).getOrder_status().equalsIgnoreCase("pending"))
            {
                holder.order_status.setImageResource(R.drawable.pending);
            }
            else if(OrdersListReView.get(position).getOrder_status().equalsIgnoreCase("cancelled"))
            {
                holder.order_status.setImageResource(R.drawable.cancel);
            }

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(v.getContext(), OrdersProductListActivity.class);
                    intent.putExtra("order_id",OrdersListReView.get(position).getId());
                    intent.putExtra("customer_name",OrdersListReView.get(position).getCustomer_name());
                    intent.putExtra("payment_mode",OrdersListReView.get(position).getPayment_mode());
                    intent.putExtra("payment_confirm",OrdersListReView.get(position).getPayment_confirmation());
                    intent.putExtra("latitude",OrdersListReView.get(position).getLatitude());
                    intent.putExtra("longitude",OrdersListReView.get(position).getLongitude());
                    v.getContext().startActivity(intent);*/
                }
            });

            //holder.distance_txt.setText(OrdersListReView.get(position).getDistance());
            //holder.time_txt.setText(OrdersListReView.get(position).getTime());
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
        TextView order_number,customer_name,payment_confirm,payment_mode,time_txt,distance_txt;
        CardView subItemCard_view;
        ImageView order_status;
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

            order_status = (ImageView) view.findViewById(R.id.order_status);
        }
    }


}
