package com.kesari.trackingfresh.TKCash.Added;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.TKCash.WalletMainPOJO;

/**
 * Created by kesari on 05/07/17.
 */

public class Added_Fragment extends Fragment {

    private static String title;
    private static int page;
    View view;
    private Gson gson;
    private WalletMainPOJO walletMainPOJO;

    public static Added_Fragment newInstance(int a,String s) {
        Added_Fragment fragmentfirst= new Added_Fragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("no.1", title);
        fragmentfirst.setArguments(args);

        return fragmentfirst;
    }

    public static Added_Fragment hideInstance(String s) {
        Added_Fragment fragmentfirst= new Added_Fragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("no.1", title);
        fragmentfirst.setArguments(args);


        return fragmentfirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_added, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("1", 0);
        title = getArguments().getString("no.1");

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
