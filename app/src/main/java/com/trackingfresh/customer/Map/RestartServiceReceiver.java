package com.trackingfresh.customer.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.trackingfresh.customer.Utilities.IOUtils;
import com.trackingfresh.customer.Utilities.SharedPrefUtil;


/**
 * Created by kesari on 19/09/17.
 */

public class RestartServiceReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPrefUtil.sendLocationUpdates(context,true);

        if (!IOUtils.isServiceRunning(LocationServiceNew.class, context)) {
            // LOCATION SERVICE
            context.startService(new Intent(context, LocationServiceNew.class));
            Log.e("SERVICE STARTED", "Location service is already running");
        }
    }

}