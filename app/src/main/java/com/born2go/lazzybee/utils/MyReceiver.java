package com.born2go.lazzybee.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.born2go.lazzybee.shared.LazzyBeeShare;

/**
 * Created by Hue on 9/4/2015.
 */
public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        int index = Integer.valueOf(intent.getStringExtra(LazzyBeeShare.NOTIFICATION_INDEX));
        //Toast.makeText(context, String.valueOf("Index: " + index), Toast.LENGTH_SHORT).show();

//        boolean start = false;
//        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
//        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
//            if (runningServiceInfo.service.getClassName().equals(MyAlarmService.class)) {
//                start = true;
//                break;
//            } else {
//                start = false;
//            }
//        }
//        if (start) {
            Intent service1 = new Intent(context, MyAlarmService.class);
            context.startService(service1);
//        }

    }

}