package com.kesari.trackingfresh.SNS;

/**
 * Created by kesari on 19/10/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kesari.trackingfresh.ProductPage.DashboardActivity;
import com.kesari.trackingfresh.R;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        //MainActivity.myMsg = remoteMessage.getNotification().getBody();

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Tracking Fresh")
                .setContentText(messageBody)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_tkf);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(id, notificationBuilder.build());
    }

    /*private void sendNotification(String message) {
        Intent myIntent = null;

        myIntent = new Intent();
        myIntent.setClass(getApplicationContext(), News.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_largeicon);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Vasai Birds")
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentText("New News Available!!!")
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_vb);
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_vb);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_image);
        bitmap = Bitmap.createScaledBitmap(bitmap, 500, 350, false);

        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)).setSubText(message);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(id, notificationBuilder.build());

    }*/
}
