package com.kesari.trackingfresh.Settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.Settings.Address.AddressSettingsFragment;
import com.kesari.trackingfresh.Settings.MyCards.CardsSettingFragment;
import com.kesari.trackingfresh.SlidingTabs.SlidingTabLayout;

public class SettingsActivity extends AppCompatActivity {

    private SlidingTabLayout tourTabs;
    private ViewPager tourPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tourTabs = (SlidingTabLayout) findViewById(R.id.tourTabSlider);
        tourPager = (ViewPager) findViewById(R.id.tourTabsPager);
        tourPager.setAdapter(new ExpertisePagerAdapter(getSupportFragmentManager()));
        //tourTabs.setDistributeEvenly(true);
        tourTabs.setViewPager(tourPager);
        tourTabs.setSelectedIndicatorColors(ContextCompat.getColor(SettingsActivity.this,R.color.colorPrimary));
    }


    class ExpertisePagerAdapter extends FragmentPagerAdapter {
        String tabs[];

        public ExpertisePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.setting);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = null;

            switch (position) {
                case 0: {
                    f = new AddressSettingsFragment();
                    break;
                }
                case 1: {
                    f = new CardsSettingFragment();
                    break;
                }

            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
