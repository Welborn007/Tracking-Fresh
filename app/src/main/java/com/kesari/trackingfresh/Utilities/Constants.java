package com.kesari.trackingfresh.Utilities;

/**
 * Created by kesari on 28/04/17.
 */

public interface Constants {

    String SheetalIP = "192.168.1.10:8000";
    String StagingIP = "192.168.1.220:8000";
    String VaibhavIP = "192.168.1.27:8000";
    String GaneshIP = "192.168.1.112:8000";
    String AlnoorIP = "192.168.1.191:8000";
    String MarutiIP = "192.168.1.148:8000";
    String LIVEIP = "52.66.75.55:8000";

    //Login API
    String LoginActivityAPI = "http://" + StagingIP + "/api/customer/login";

    //Register API
    String RegisterActivityAPI = "http://" + StagingIP + "/api/customer/";

    //Driver Location API - Product_Fragment
    //String LocationAPI = "http://"+ StagingIP +"/api/vehicle_positions/by_driver_id/dr001";

    //Nearest Vehicle Check
    String CheckNearestVehicle = "http://" + StagingIP + "/api/vehicle_positions/nearest/";

    //Product Category
    String Product_Category = "http://" + StagingIP + "/api/productCategory/vehicle/list?vehicleId=";

    //Product Desc
    String Product_Desc = "http://" + StagingIP + "/api/product/byCategory/";

    //Verify Mobile No. // Params - User ID // Get String Request
    String VerifyMobile = "http://" + StagingIP + "/api/customer/isMobile/";

    //Send OTP // Params - mobileNo , User ID // JSON Object Request
    String SendOTP = "http://" + StagingIP + "/api/OTP/sendOtp/";

    //Match OTP // Params - otp , mobileNo, User ID
    String MatchOTP  = "http://" + StagingIP + "/api/OTP/matchOtp";

    //Profile
    String Profile = "http://" + StagingIP +"/api/customer/profile";

    //Profile Edit
    String ProfileEdit = "http://" + StagingIP + "/api/customer/";

    //Profile Image Upload
    String ProfileImagePath = "http://" + StagingIP + "/api/upload";

    //Add Delivery Address
    String NewAddress = "http://" + StagingIP + "/api/address/add";

    //Fetch Address
    String FetchAddress =  "http://" + StagingIP + "/api/address/";

    //Update Address
    String UpdateAddress = "http://" + StagingIP + "/api/address/update/";

    //Delete Address
    String DeleteAddress = "http://" + StagingIP + "/api/address/delete/";

    //Get Fare
    String GetFare = "http://" + StagingIP + "/api/order/getfare";

    //Order List From Cart
    String AddOrder = "http://" + StagingIP + "/api/order/add";

    //Get Order List
    String OrderList = "http://" + StagingIP + "/api/order/orderList";

    //Update Order after payment Put Request
    String UpdateOrder = "http://" + StagingIP + "/api/order/update";

    //get Order details from order id
    String OrderDetails = "http://"+ StagingIP +"/api/order/";

    //Wallet All Transactions
    String AllWalletTransactions = "http://"+ StagingIP + "/api/userWallet/";

    //Wallet Paid Transactions
    String PaidWalletTransactions = "http://"+ StagingIP + "/api/userWallet/?operation=minus";

    //Wallet Received Transactions
    String ReceivedWalletTransactions = "http://"+ StagingIP + "/api/userWallet/?operation=add";

    //Check Promocode Validity
    String PromocodeValidity = "http://"+ StagingIP + "/api/promoCode/validate";

    //TODO Forget Password
    String ForgetPassword = "http://"+ StagingIP + "/api/customer/forgotPassword";

    //Verify Duplicate Mobile No
    String VerifyDuplicate = "http://"+ StagingIP + "/api/customer/mobile/";

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
