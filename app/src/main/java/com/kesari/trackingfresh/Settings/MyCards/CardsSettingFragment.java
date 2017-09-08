package com.kesari.trackingfresh.Settings.MyCards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kesari.trackingfresh.R;

/**
 * Created by kesari on 06/09/17.
 */

public class CardsSettingFragment extends Fragment {


    private String TAG = this.getClass().getSimpleName();

    public CardsSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_settings, container, false);



        return view;
    }
}
