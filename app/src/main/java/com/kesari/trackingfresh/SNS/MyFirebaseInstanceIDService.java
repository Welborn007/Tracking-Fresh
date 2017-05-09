package com.kesari.trackingfresh.SNS;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    String token;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Inside"," firebase");
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        token = FirebaseInstanceId.getInstance().getToken();

        sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
        editorLogin = sharedpreferencesLogin.edit();
        editorLogin.putString("token", token);
        editorLogin.apply();

        /*try {
            Navigation.myFireBaseToken = refreshedToken;
            Navigation.editor.putString("firebasetoken", refreshedToken);
            Navigation.editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }

}
