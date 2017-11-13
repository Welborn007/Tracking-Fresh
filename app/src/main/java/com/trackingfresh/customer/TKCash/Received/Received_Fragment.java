package com.trackingfresh.customer.TKCash.Received;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.TKCash.WalletMainPOJO;
import com.trackingfresh.customer.TKCash.WalletRecycler_Adapter;
import com.trackingfresh.customer.Utilities.Constants;
import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kesari on 05/07/17.
 */

public class Received_Fragment extends Fragment {

    private static String title;
    private static int page;
    View view;

    private String TAG = this.getClass().getSimpleName();
    private Gson gson;
    private WalletMainPOJO walletMainPOJO;

    public  RecyclerView.Adapter adapter;
    public  RecyclerView recListOrders;
    public LinearLayoutManager Orders;

    public static Received_Fragment newInstance(int a,String s) {
        Received_Fragment fragmentfirst= new Received_Fragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("no.1", title);
        fragmentfirst.setArguments(args);

        return fragmentfirst;
    }

    public static Received_Fragment hideInstance(String s) {
        Received_Fragment fragmentfirst= new Received_Fragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("no.1", title);
        fragmentfirst.setArguments(args);


        return fragmentfirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_received, container, false);

        recListOrders = (RecyclerView) view.findViewById(R.id.recyclerView);

        recListOrders.setHasFixedSize(true);
        Orders = new LinearLayoutManager(getActivity());
        Orders.setOrientation(LinearLayoutManager.VERTICAL);
        recListOrders.setLayoutManager(Orders);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("1", 0);
        title = getArguments().getString("no.1");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gson = new Gson();
        fetchAllWalletTransaction();
    }

    private void fetchAllWalletTransaction() {
        try {

            String url = Constants.ReceivedWalletTransactions;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    fetchAllWalletResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void fetchAllWalletResponse(String Response) {
        try {

            walletMainPOJO = gson.fromJson(Response, WalletMainPOJO.class);

            if(walletMainPOJO.getData().isEmpty())
            {
                //FireToast.customSnackbar(getActivity(),"No Transaction done!!","Swipe");
            }
            else
            {
                adapter = new WalletRecycler_Adapter(walletMainPOJO.getData(),getActivity());
                recListOrders.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
