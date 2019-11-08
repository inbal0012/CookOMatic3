package com.example.adopy.Utilities.Receivers_and_Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.adopy.R;
import com.example.adopy.Activities.SearchFragment;

import static com.example.adopy.Utilities.Models.App.WEEKLY_UPDATES_CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("my_AlarmReceiver", "onReceive: ");

        Intent taoIntent = new Intent(context, SearchFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,taoIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentText(context.getString(R.string.weeklyUpdatesChannelDesc))
                .setContentTitle(context.getString(R.string.weeklyUpdatesChannelTitle))
                .setSmallIcon(R.drawable.foot)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(WEEKLY_UPDATES_CHANNEL_ID, context.getString(R.string.weeklyUpdatesChannelName), NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(WEEKLY_UPDATES_CHANNEL_ID);
        }

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        //notification.flags |= Notification.FLAG_INSISTENT;

        manager.notify(1,notification);
    }
}
