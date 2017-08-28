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
    String LoginActivityAPI = "http://" + LIVEIP + "/api/customer/login";

    //Register API
    String RegisterActivityAPI = "http://" + LIVEIP + "/api/customer/";

    //Driver Location API - Product_Fragment
    //String LocationAPI = "http://"+ LIVEIP +"/api/vehicle_positions/by_driver_id/dr001";

    //Nearest Vehicle Check
    String CheckNearestVehicle = "http://" + LIVEIP + "/api/vehicle_positions/nearest/";

    //Product Category
    String Product_Category = "http://" + LIVEIP + "/api/productCategory/vehicle/list?vehicleId=";

    //Product Desc
    String Product_Desc = "http://" + LIVEIP + "/api/product/byCategory/";

    //Verify Mobile No. // Params - User ID // Get String Request
    String VerifyMobile = "http://" + LIVEIP + "/api/customer/isMobile/";

    //Send OTP // Params - mobileNo , User ID // JSON Object Request
    String SendOTP = "http://" + LIVEIP + "/api/OTP/sendOtp/";

    //Match OTP // Params - otp , mobileNo, User ID
    String MatchOTP  = "http://" + LIVEIP + "/api/OTP/matchOtp";

    //Profile
    String Profile = "http://" + LIVEIP +"/api/customer/profile";

    //Profile Edit
    String ProfileEdit = "http://" + LIVEIP + "/api/customer/";

    //Profile Image Upload
    String ProfileImagePath = "http://" + LIVEIP + "/api/upload";

    //Add Delivery Address
    String NewAddress = "http://" + LIVEIP + "/api/address/add";

    //Fetch Address
    String FetchAddress =  "http://" + LIVEIP + "/api/address/";

    //Update Address
    String UpdateAddress = "http://" + LIVEIP + "/api/address/update/";

    //Delete Address
    String DeleteAddress = "http://" + LIVEIP + "/api/address/delete/";

    //Get Fare
    String GetFare = "http://" + LIVEIP + "/api/order/getfare";

    //Order List From Cart
    String AddOrder = "http://" + LIVEIP + "/api/order/add";

    //Get Order List
    String OrderList = "http://" + LIVEIP + "/api/order/orderList";

    //Update Order after payment Put Request
    String UpdateOrder = "http://" + LIVEIP + "/api/order/update";

    //get Order details from order id
    String OrderDetails = "http://"+ LIVEIP +"/api/order/";

    //Wallet All Transactions
    String AllWalletTransactions = "http://"+ LIVEIP + "/api/userWallet/";

    //Wallet Paid Transactions
    String PaidWalletTransactions = "http://"+ LIVEIP + "/api/userWallet/?operation=minus";

    //Wallet Received Transactions
    String ReceivedWalletTransactions = "http://"+ LIVEIP + "/api/userWallet/?operation=add";

    //Check Promocode Validity
    String PromocodeValidity = "http://"+ LIVEIP + "/api/promoCode/validate";

    //TODO Forget Password
    String ForgetPassword = "http://"+ LIVEIP + "/api/customer/forgotPassword";

    //Verify Duplicate Mobile No
    String VerifyDuplicate = "http://"+ LIVEIP + "/api/customer/mobile/";

    //Cancellation / Rejection reasons
    String Reasons = "http://"+ LIVEIP + "/api/order/reason?reason=";

    //Send Firebase Token
    String FirebaseToken =  "http://"+ LIVEIP + "/api/customer/fbt";

    //Vehicle Route
    String VehicleRoute = "http://"+ LIVEIP + "/api/customer/getVehicleRoutes/";

    //Vehicle Socket Location

    String VehicleLiveLocation = "http://" + LIVEIP;

    //Vehicle Nearest Route

    String VehicleNearestRoute = "http://"+ LIVEIP +  "/api/vehicleTimeTable/nearestRoute";

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
