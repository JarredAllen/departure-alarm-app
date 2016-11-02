package com.example.jarred.departurealarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

/**
 * Service by this app to deliver notifications to the user and ignore outdated ones.
 *
 * @author Jarred
 * @version 10/31/2016
 */
public class DeliverNotifications extends IntentService {

    private static final String packageName="com.example.jarred.departurealarm";

    private String eventName;

    public DeliverNotifications() {
        super("DeliverNotifications");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventName=intent.getStringExtra(packageName+".eventName");
        long time=Long.parseLong(intent.getStringExtra(packageName+".loadTime"));
        if(time>=Long.parseLong(DatabaseRetriever.getLastLoadTime())) {
            Notification.Builder notificationBuilder = new Notification.Builder(this).setSmallIcon(R.drawable.event_notification_icon).setContentTitle("Time to leave")
                    .setContentText("Time to leave for " + eventName);//Moved this line down to aid code readability in Android Studio with my view settings
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, notificationBuilder.build());
        }
    }
}
