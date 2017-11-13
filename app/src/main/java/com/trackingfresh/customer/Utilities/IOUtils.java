package com.trackingfresh.customer.Utilities;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.trackingfresh.customer.Map.LocationServiceNew;
import com.trackingfresh.customer.R;
import com.trackingfresh.customer.network.FireToast;
import com.trackingfresh.customer.network.MyApplication;
import com.nispok.snackbar.listeners.ActionSwipeListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by kesari on 13/04/17.
 */

public class IOUtils {

    private Gson gson;
    ErrorPOJO errorPOJO;

    public static DraweeController getFrescoImageController(Context context, String url) {

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {

            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

            }

            @Override
            public void onFailure(String id, Throwable throwable) {

            }
        };

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(url)
                // other setters
                .build();

        return controller;
    }

    public static GenericDraweeHierarchy getFrescoImageHierarchy(Context context) {


        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .build();


        return hierarchy;
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, Context context) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public static String roundToOneDigit(float paramFloat) {
        return String.format("%.2f%n", paramFloat);
    }

    public static void slideToRight(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from right to left
    public static void slideToLeft(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from top to bottom
    public static void slideToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to top
    public static void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
/*
    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
*/

    // Volley String Get Request
    public void getGETStringRequest(final Context context, String url, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);
        // custom dialog
        /*final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();*/

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DriverResponse", response.toString());

                callback.onSuccess(response);

                //dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                //dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    failureCallback.onFailure(ErrorResponse(json, context));

                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());
                    new SweetAlertDialog(context)
                            .setTitleText("Oops Something Went Wrong!!")
                            .show();
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(stringRequest, "");
    }

    // Volley String Get Request with Header
    public void getGETStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        Log.i("url", url);

        /*final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();*/

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        //dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        //dialog.dismiss();

                        try {
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                            failureCallback.onFailure(ErrorResponse(json, context));

                        } catch (Exception e) {
                            //Log.d("Error", e.getMessage());
                            /*new SweetAlertDialog(context)
                                    .setTitleText("Oops Something Went Wrong!!")
                                    .show();*/
                            e.printStackTrace();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    // Volley String Delete Request with Header
    public void getDeleteStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        Log.i("url", url);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        dialog.dismiss();

                        try {
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                            failureCallback.onFailure(ErrorResponse(json, context));

                        } catch (Exception e) {
                            //Log.d("Error", e.getMessage());
                            new SweetAlertDialog(context)
                                    .setTitleText("Oops Something Went Wrong!!")
                                    .show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    // Volley String POST Request with Header
    public void getPOSTStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Log.i("url", url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        dialog.dismiss();

                        try {
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                            failureCallback.onFailure(ErrorResponse(json, context));

                        } catch (Exception e) {
                            //Log.d("Error", e.getMessage());
                            new SweetAlertDialog(context)
                                    .setTitleText("Oops Something Went Wrong!!")
                                    .show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    public interface VolleyCallback {
        void onSuccess(String result);
    }

    public interface VolleyFailureCallback {
        void onFailure(String result);
    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequest(final Context context, String url, JSONObject jsonObject, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    failureCallback.onFailure(ErrorResponse(json, context));

                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());
                    new SweetAlertDialog(context)
                            .setTitleText("Oops Something Went Wrong!!")
                            .show();
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request for Dialog
    public void sendJSONObjectRequestHeaderDialog(final Context context, final ViewGroup viewGroup, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    //ErrorResponseDialog(json,context,viewGroup);

                    failureCallback.onFailure(ErrorResponseDialog(json, context, viewGroup));

                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbarDialog(context, "Oops Something Went Wrong!!", "", viewGroup, new ActionSwipeListener() {
                        @Override
                        public void onSwipeToDismiss() {
                            viewGroup.setVisibility(View.GONE);
                        }
                    });
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };
        ;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        /*Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();*/

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        //dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                //dialog.dismiss();

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    failureCallback.onFailure(ErrorResponse(json, context));

                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.d("Error", e.getMessage());
                    /*new SweetAlertDialog(context)
                            .setTitleText("Oops Something Went Wrong!!")
                            .show();*/
                    //FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };
        ;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Put Request
    public void sendJSONObjectPutRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback, final VolleyFailureCallback failureCallback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());

                try {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    failureCallback.onFailure(ErrorResponse(json, context));

                } catch (Exception e) {
                    //Log.d("Error", e.getMessage());
                   /* new SweetAlertDialog(context)
                            .setTitleText("Oops Something Went Wrong!!")
                            .show();*/
                    //FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };
        ;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    private String ErrorResponse(String Response, Context context) {
        gson = new Gson();
        errorPOJO = gson.fromJson(Response, ErrorPOJO.class);
        String FailureReason = "Oops Something Went Wrong!!";

        if (errorPOJO.getErrors() != null) {
            String[] error = errorPOJO.getErrors();
            String errorString = error[0];

            //FireToast.customSnackbar(context, errorString,"");

            new SweetAlertDialog(context)
                    .setTitleText(errorString)
                    .show();

            FailureReason = errorString;
        } else if (errorPOJO.getMessage() != null) {
            new SweetAlertDialog(context)
                    .setTitleText(errorPOJO.getMessage())
                    .show();

            FailureReason = errorPOJO.getMessage();
            //FireToast.customSnackbar(context, errorPOJO.getMessage(),"");
        } else {
            /*new SweetAlertDialog(context)
                    .setTitleText("Oops Something Went Wrong!!")
                    .show();*/

            FailureReason = "Oops Something Went Wrong!!";
            //FireToast.customSnackbar(context, "Oops Something Went Wrong!!","");
        }

        return FailureReason;
    }

    private String ErrorResponseDialog(String Response, Context context, final ViewGroup viewGroup) {
        gson = new Gson();
        errorPOJO = gson.fromJson(Response, ErrorPOJO.class);
        String FailureReason = "Oops Something Went Wrong!!";

        if (errorPOJO.getErrors() != null) {
            String[] error = errorPOJO.getErrors();
            String errorString = error[0];

            FireToast.customSnackbarDialog(context, errorString, "", viewGroup, new ActionSwipeListener() {
                @Override
                public void onSwipeToDismiss() {
                    viewGroup.setVisibility(View.GONE);
                }
            });

            FailureReason = errorString;

        } else if (errorPOJO.getMessage() != null) {
            FireToast.customSnackbarDialog(context, errorPOJO.getMessage(), "", viewGroup, new ActionSwipeListener() {
                @Override
                public void onSwipeToDismiss() {
                    viewGroup.setVisibility(View.GONE);
                }
            });

            FailureReason = errorPOJO.getMessage();
        } else {
            FireToast.customSnackbarDialog(context, "Oops Something Went Wrong!!", "", viewGroup, new ActionSwipeListener() {
                @Override
                public void onSwipeToDismiss() {
                    viewGroup.setVisibility(View.GONE);
                }
            });

            FailureReason = "Oops Something Went Wrong!!";
        }

        return FailureReason;
    }

    public static void showRipples(LatLng latLng, GoogleMap map, int DURATION) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(500, 500);
        d.setColor(0x5500ff00);
        d.setStroke(0, Color.TRANSPARENT);

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        // Radius of the circle
        final int radius = 100;

        // Add the circle to the map
        final GroundOverlay circle = map.addGroundOverlay(new GroundOverlayOptions()
                .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

        // Prep the animator
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 0, radius);
        PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setValues(radiusHolder, transparencyHolder);
        valueAnimator.setDuration(DURATION);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
                float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
                circle.setDimensions(animatedRadius * 2);
                circle.setTransparency(animatedAlpha);
            }
        });

        // start the animation
        valueAnimator.start();
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        if (!IOUtils.isServiceRunning(LocationServiceNew.class, context)) {
                            // LOCATION SERVICE
                            context.startService(new Intent(context, LocationServiceNew.class));
                            Log.e("IOUTILS", "Location service is already running");
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
