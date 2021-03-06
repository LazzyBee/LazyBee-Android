package com.born2go.lazzybee.utils;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.MainActivity;
import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * Created by Hue on 9/4/2015.
 */
public class MyAlarmService extends Service {

    private static final String TAG = "MyAlarmService";
    private NotificationManager mManager;
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    SharedPreferences sp;
    private Intent intent;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    private void showNotification() {
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,LazzyBeeShare.APP_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_drawer)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_message)).setAutoCancel(true);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity
                (this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingNotificationIntent);

        mManager.notify(0, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        mManager.cancel(0);
        super.onDestroy();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_LONG).show();
    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//        showNotification();
//        Log.i(TAG, "Service running");
//    }
        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");

        showNotification();

        Log.i(TAG, "Service running");
        return Service.START_NOT_STICKY;
    }


}