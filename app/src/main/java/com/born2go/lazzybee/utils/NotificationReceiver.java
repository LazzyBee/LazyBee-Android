package com.born2go.lazzybee.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.SplashScreen;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * Created by Hue on 9/4/2015.
 */
public class NotificationReceiver extends BroadcastReceiver {


    private static final String TAG = "NotificationReceiver";
    public static final String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static final String NOTIFICATION_WHEN = "when";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BroadcastReceiver::OnReceive() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //get id Notificaion
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        //get alertTime Notification
        long alertTime = intent.getLongExtra(NOTIFICATION_WHEN, 0);

        //Define intent and pendingInten MainActivity
        Intent intent1 = new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String message;
        try {
            //Container container = LazzyBeeSingleton.getContainerHolder().getContainer();
//            if (container == null) {
            message = context.getString(R.string.notification_message);
//            } else {
//                message = container.getString(LazzyBeeShare.NOTIFY_TEXT);
//                if (message == null) {
//                    message = context.getString(R.string.notification_message);
//                }
//            }
        } catch (Exception e) {
            message = context.getString(R.string.notification_message);
        }


        //Define notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, LazzyBeeShare.APP_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_logo_green)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setWhen(alertTime)
                        .setSound(alarmSound);

        mBuilder.setContentIntent(pendingIntent);
        try {
            if (notificationManager != null) {
                //Noti Notification
                String onoffNotification = LazzyBeeSingleton.learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_NOTIFICTION);
                switch (onoffNotification) {
                    case LazzyBeeShare.ON:
                        notificationManager.notify(id, mBuilder.build());
                        break;
                    case LazzyBeeShare.OFF:
                        Log.d(TAG, "Off notification");
                        break;
                    default:
                        Log.d(TAG, "Notification null-->ON");
                        notificationManager.notify(id, mBuilder.build());
                        break;
                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }


    }

}