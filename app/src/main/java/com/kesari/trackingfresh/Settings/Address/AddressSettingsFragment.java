package com.kesari.trackingfresh.Settings.Address;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.kesari.trackingfresh.DeliveryAddress.AddDeliveryAddress.Add_DeliveryAddress;
import com.kesari.trackingfresh.DeliveryAddress.AddressPOJO;
import com.kesari.trackingfresh.DeliveryAddress.DefaultDeliveryAddress.FetchAddressPOJO;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Utilities.Constants;
import com.kesari.trackingfresh.Utilities.IOUtils;
import com.kesari.trackingfresh.Utilities.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by kesari on 06/09/17.
 */

public class AddressSettingsFragment extends Fragment {

    public static List<AddressPOJO> addressArrayList = new ArrayList<>();

    public static Gson gson;
    public static FetchAddressPOJO fetchAddressPOJO;
    public static RecyclerView recListFecthedDeliveryAddress;
    private static LinearLayoutManager AddressLayoutManager;
    public static RecyclerView.Adapter adapterAddress;
    private Button btnSubmit;
    public static boolean default_address = false;
    private String TAG = this.getClass().getSimpleName();

    public AddressSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address_settings, container, false);

        gson = new Gson();
        recListFecthedDeliveryAddress = (RecyclerView) view.findViewById(R.id.recyclerView);

        recListFecthedDeliveryAddress.setHasFixedSize(true);
        AddressLayoutManager = new LinearLayoutManager(getActivity());
        AddressLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recListFecthedDeliveryAddress.setLayoutManager(AddressLayoutManager);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), Add_DeliveryAddress.class);
                intent.putExtra("value","SettingAddress");
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchUserAddress(getActivity(),TAG);
    }

    public static void fetchUserAddress(final Context context, final String TAG) {
        try {

            String url = Constants.FetchAddress;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    fetchUserAddressResponse(result,context,TAG);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void fetchUserAddressResponse(String Response,Context context, final String TAG) {
        try {

            default_address = false;
            fetchAddressPOJO = gson.fromJson(Response, FetchAddressPOJO.class);

            if (fetchAddressPOJO.getData().isEmpty()) {

                adapterAddress = new UpdateSettingsAddress_RecyclerAdpater(fetchAddressPOJO.getData(),context);
                recListFecthedDeliveryAddress.setAdapter(adapterAddress);
            } else {

                addressArrayList = fetchAddressPOJO.getData();

                adapterAddress = new UpdateSettingsAddress_RecyclerAdpater(fetchAddressPOJO.getData(),context);
                recListFecthedDeliveryAddress.setAdapter(adapterAddress);

                for (Iterator<AddressPOJO> it = addressArrayList.iterator(); it.hasNext(); ) {
                    AddressPOJO addressPOJO = it.next();

                    if (addressPOJO.isDefault())
                    {
                        default_address = true;
                    }
                    else
                    {

                    }

                }

                if(!default_address)
                {
                    //FireToast.customSnackbar(context, "Default address not set!", "");
                    //Toast.makeText(context,"Default address not set!", Toast.LENGTH_SHORT).show();

                    new SweetAlertDialog(context)
                            .setTitleText("Default address not set!")
                            .show();
                }

            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void updateDeliveryAddress(String addressID, final int position, final Context context) {
        try {

            String url = Constants.UpdateAddress;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", addressID);
                postObject.put("isDefault", "true");

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.sendJSONObjectPutRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Address Update", result.toString());
                    updateDeliveryAddressResponse(result,position,context);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateDeliveryAddressResponse(String Response,int pos,Context context)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String Message = jsonObject.getString("message");

            if(Message.equalsIgnoreCase("Updated Successfully"))
            {
                adapterAddress.notifyDataSetChanged();
                fetchUserAddress(context,"Address Update");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
