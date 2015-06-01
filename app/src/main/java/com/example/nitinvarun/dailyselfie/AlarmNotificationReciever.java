package com.example.nitinvarun.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by NitinVarun on 5/24/2015.
 */
public class AlarmNotificationReciever extends BroadcastReceiver{ // when alarm is triggered it is handled by this class

    CharSequence tickerText = "Take Your Selfie";
    CharSequence contentTitle = "DailySelfie";
    CharSequence contentText = "Take Selfie";

    Intent mIntent;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mIntent = new Intent(context,MainActivity.class);

        mPendingIntent = PendingIntent.getActivity(context,0,mIntent,0);

        Notification.Builder builder = new Notification.Builder(context) // creating Notification after receiving alarm
                .setTicker(tickerText)
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(mPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,builder.build());
    }
}
