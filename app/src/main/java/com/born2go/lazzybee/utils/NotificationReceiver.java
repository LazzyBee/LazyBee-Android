package com.born2go.lazzybee.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.MainActivity;
import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * Created by Hue on 9/4/2015.
 */
public class NotificationReceiver extends BroadcastReceiver {


    private static final String TAG = "NotificationReceiver";
//    private static int MID = 0;
//    int[] hour;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BroadcastReceiver::OnReceive() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        int mId = intent.getIntExtra(LazzyBeeShare.INIT_NOTIFICATION, 0);
        createNotification(context, context.getString(R.string.app_name), context.getString(R.string.notificaion_message), context.getString(R.string.notificaion_message), mId);
//        hour = context.getResources().getIntArray(R.array.notification_hours);
//        Intent service1 = new Intent(context, MyAlarmService.class);
//        context.startService(service1);

//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        boolean start = false;
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            Log.i("NotificationReceiver", "Services name:" + service.service.getClassName() + ",My Services:" + MyAlarmService.class.getName());
//            if (!MyAlarmService.class.getName().equals(service.service.getClassName())) {
//                start = true;
//            }
//        }
//        if (start) {
//            Log.i("NotificationReceiver", "Service not start!");
//        }

//        int setNotiCode = intent.getIntExtra(LazzyBeeShare.INIT_NOTIFICATION, 0);
//
//        Calendar currentTimeStamp = Calendar.getInstance();
//        currentTimeStamp.set(Calendar.HOUR_OF_DAY, hour[setNotiCode]);
//        currentTimeStamp.set(Calendar.MINUTE, 0);
//        currentTimeStamp.set(Calendar.SECOND, 0);
//        long when = currentTimeStamp.getTimeInMillis();//System.currentTimeMillis();
//
//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
//                context).setSmallIcon(R.drawable.ic_drawer)
//                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText(context.getString(R.string.notificaion_message))
//                .setSound(alarmSound)
//                .setAutoCancel(true)
//                .setWhen(when)
////                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}
//                .setContentIntent(pendingIntent);
//        Log.i(TAG, currentTimeStamp.getTimeInMillis() + ":" + when);
//
//        //setNotiCode == 0 khong phai la setup
//        //setNotiCode == 1 setup
//        notificationManager.notify(MID, mNotifyBuilder.build());
//        MID++;

//        if (setNotiCode == 0) {
//            Log.i(TAG, "Show notification");
//        } else {
//            Log.i(TAG, "SetUp Notification!Not Show notification");
//        }

    }


    public void createNotification(Context context, String msg, String msgText, String msgAlert, int mId) {

        // Define an Intent and an action to perform with it by another application
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        // Builds a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_logo_green)

                        .setContentTitle(msg)
                        .setTicker(msgAlert)
                        .setContentText(msgText);

        // Defines the Intent to fire when the notification is clicked
        mBuilder.setContentIntent(notificIntent);

        // Set the default notification option
        // DEFAULT_SOUND : Make sound
        // DEFAULT_VIBRATE : Vibrate
        // DEFAULT_LIGHTS : Use the default light notification
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        // Auto cancels the notification when clicked on in the task bar
        mBuilder.setAutoCancel(true);

        // Gets a NotificationManager which is used to notify the user of the background event
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        mNotificationManager.notify(mId, mBuilder.build());

    }

}