package com.kesari.trackingfresh.Utilities;

/**
 * Created by kesari on 28/04/17.
 */

public interface Constants {

    String SheetalIP = "192.168.1.10:8000";
    String StagingIP = "192.168.1.220:8000";
    String VaibhavIP = "192.168.1.121:8000";
    String GaneshIP = "192.168.1.112:8000";
    String AlnoorIP = "192.168.1.191:8000";
    String MarutiIP = "192.168.1.148:8000";
    String LIVEIP = "52.66.75.55:8000";

    //Login API
    String LoginActivityAPI = "http://" + LIVEIP + "/api/customer/login";

    //Register API
    String RegisterActivityAPI = "http://" + LIVEIP + "/api/customer/";

    //Driver Location API - Product_Fragment
    String LocationAPI = "http://"+ LIVEIP +"/api/vehicle_positions/by_driver_id/dr001";

    //Product Category
    String Product_Category = "http://" + LIVEIP + "/api/productCategory/list/";

    //Product Desc
    String Product_Desc = "http://" + LIVEIP + "/api/product/byCategory/";

    //Verify Mobile No. // Params - User ID // Get String Request
    String VerifyMobile = "http://" + GaneshIP + "/api/customer/isMobile/";

    //Send OTP // Params - mobileNo , User ID // JSON Object Request
    String SendOTP = "http://" + GaneshIP + "/api/OTP/sendOtp/";

    //Match OTP // Params - otp , mobileNo, User ID
    String MatchOTP  = "http://" + GaneshIP + "/api/OTP/matchOtp";

    //Profile
    String Profile = "http://" + LIVEIP +"/api/customer/profile";

    //Add Delivery Address
    String NewAddress = "http://" + GaneshIP + "/api/address/add";

    //Fetch Address
    String FetchAddress =  "http://" + GaneshIP + "/api/address/";

    //Update Address
    String UpdateAddress = "http://" + GaneshIP + "/api/address/update/";

    //Delete Address
    String DeleteAddress = "http://" + GaneshIP + "/api/address/delete/";

    //Order List From Cart
    String AddOrder = "http://" + GaneshIP + "/api/order/add";

    //Get Order List
    String OrderList = "http://" + GaneshIP + "/api/order/orderList";

    //Update Order after payment Put Request
    String UpdateOrder = "http://" + GaneshIP + "/api/order/update";

    //get Order details from order id
    String OrderDetails = "http://"+ GaneshIP +"/api/order/";

    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    public static final int FASTEST_INTERVAL_IN_SECONDS = 3;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
}
