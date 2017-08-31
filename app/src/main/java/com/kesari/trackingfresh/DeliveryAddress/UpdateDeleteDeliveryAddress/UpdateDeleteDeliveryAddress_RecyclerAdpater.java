package com.kesari.trackingfresh.DeliveryAddress.UpdateDeleteDeliveryAddress;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.UpdateDeliveryAddress.UpdateDeliveryAddressActivity;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 06/06/17.
 */

public class UpdateDeleteDeliveryAddress_RecyclerAdpater extends RecyclerView.Adapter<UpdateDeleteDeliveryAddress_RecyclerAdpater.RecyclerViewHolder>
{
    List<AddressPOJO> AddressPOJOs;
    Context context;
    private String TAG = this.getClass().getSimpleName();
    RadioButton selected=null;

    public UpdateDeleteDeliveryAddress_RecyclerAdpater(List<AddressPOJO> AddressPOJOs, Context context)
    {
        this.AddressPOJOs = AddressPOJOs;
        this.context = context;
    }

    @Override
    public UpdateDeleteDeliveryAddress_RecyclerAdpater.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_delete_delivery_address_row_layout,parent,false);

        UpdateDeleteDeliveryAddress_RecyclerAdpater.RecyclerViewHolder recyclerViewHolder = new UpdateDeleteDeliveryAddress_RecyclerAdpater.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final UpdateDeleteDeliveryAddress_RecyclerAdpater.RecyclerViewHolder holder, final int position) {

        try
        {

            //holder.selected_address.setChecked(AddressPOJOs.get(position).isDefault());

            if(AddressPOJOs.get(position).isDefault())
            {
                holder.default_text.setBackgroundColor(ContextCompat.getColor(context,R.color.button_green));
                holder.default_text.setText("Default Address");
            }
            else
            {
                holder.default_text.setBackgroundColor(ContextCompat.getColor(context,R.color.red_focus));
                holder.default_text.setText("Set as Default Address");
            }

            holder.customer_name.setText(AddressPOJOs.get(position).getFullName());
            holder.contact_number.setText(AddressPOJOs.get(position).getMobileNo());
            holder.address.setText(AddressPOJOs.get(position).getFlat_No() + ", " + AddressPOJOs.get(position).getBuildingName() + ", " + AddressPOJOs.get(position).getLandmark()+ ", " + AddressPOJOs.get(position).getCity()+ ", " + AddressPOJOs.get(position).getState()+ ", " + AddressPOJOs.get(position).getPincode());

            holder.addressType.setText(AddressPOJOs.get(position).getAddress_Type());

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDeliveryAddress(AddressPOJOs.get(position).get_id(),position);

                    //Toast.makeText(context, "Clicked delete", Toast.LENGTH_SHORT).show();
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "Clicked edit", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, UpdateDeliveryAddressActivity.class);
                    intent.putExtra("_id",AddressPOJOs.get(position).get_id());
                    intent.putExtra("fullName",AddressPOJOs.get(position).getFullName());
                    intent.putExtra("email_Id",AddressPOJOs.get(position).getEmail_Id());
                    intent.putExtra("mobileNo",AddressPOJOs.get(position).getMobileNo());
                    intent.putExtra("flat_No",AddressPOJOs.get(position).getFlat_No());
                    intent.putExtra("buildingName",AddressPOJOs.get(position).getBuildingName());
                    intent.putExtra("landmark",AddressPOJOs.get(position).getLandmark());
                    intent.putExtra("city",AddressPOJOs.get(position).getCity());
                    intent.putExtra("state",AddressPOJOs.get(position).getState());
                    intent.putExtra("pincode",AddressPOJOs.get(position).getPincode());
                    intent.putExtra("address_Type",AddressPOJOs.get(position).getAddress_Type());
                    intent.putExtra("latitude",AddressPOJOs.get(position).getLatitude());
                    intent.putExtra("longitude",AddressPOJOs.get(position).getLongitude());
                    intent.putExtra("isDefault",AddressPOJOs.get(position).isDefault());
                    context.startActivity(intent);
                }
            });

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "Card Clicked", Toast.LENGTH_SHORT).show();

                    if(!AddressPOJOs.get(position).isDefault())
                    {
                        FetchedDeliveryAddressActivity.updateDeliveryAddress(AddressPOJOs.get(position).get_id(),position,context);
                    }
                    else
                    {
                        Toast.makeText(context, "Address already set default", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }


    }

    @Override
    public int getItemCount() {

        return AddressPOJOs.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RadioGroup selected_addressGroup;
        RadioButton selected_address;
        TextView customer_name,contact_number,address,addressType,default_text;
        FancyButton delete,edit;
        CardView subItemCard_view;

        public RecyclerViewHolder(View view)
        {
            super(view);
            customer_name = (TextView) view.findViewById(R.id.customer_name);
            contact_number = (TextView) view.findViewById(R.id.contact_number);
            address = (TextView) view.findViewById(R.id.address);
            addressType = (TextView) view.findViewById(R.id.addressType);
            delete = (FancyButton) view.findViewById(R.id.delete);
            edit = (FancyButton) view.findViewById(R.id.edit);

            selected_address = (RadioButton) view.findViewById(R.id.selected_address);
            selected_addressGroup = (RadioGroup) view.findViewById(R.id.selected_addressGroup);
            default_text = (TextView) view.findViewById(R.id.default_text);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);

            /*selected_address.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(selected != null)
                    {
                        selected.setChecked(false);
                    }

                    selected_address.setChecked(true);
                    selected = selected_address;
                }
            });*/
        }
    }

    private void deleteDeliveryAddress(String addressID, final int position) {
        try {

            String url = Constants.DeleteAddress + addressID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getDeleteStringRequestHeader(context, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    deleteDeliveryAddressResponse(result,position);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void deleteDeliveryAddressResponse(String Response,int pos)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(Message.equalsIgnoreCase("removed Successfully!"))
            {
                FetchedDeliveryAddressActivity.fetchUserAddress(context,"AdapterAddress");
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}