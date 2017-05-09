package com.kesari.trackingfresh.Utilities;

/**
 * Created by kesari on 28/04/17.
 */

public interface Constants {

    String SheetalIP = "192.168.1.10:8000";
    String StagingIP = "192.168.1.220:8000";
    String LIVEIP = "115.112.155.181:8000";

    //Login API
    String LoginActivityAPI = "http://" + LIVEIP + "/api/customer/login";

    //Register API
    String RegisterActivityAPI = "http://" + LIVEIP + "/api/customer/";

    //Driver Location API - Product_Fragment
    String LocationAPI = "http://"+ LIVEIP +"/api/vehicle_positions/by_driver_id/dr001";

}
