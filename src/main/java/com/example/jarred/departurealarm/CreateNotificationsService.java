package com.example.jarred.departurealarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service that handles notifying the user of things that are going on in the background.
 *
 * @author Jarred
 * @version 10/30/2016
 */
public class CreateNotificationsService extends IntentService {

    public CreateNotificationsService() {
        super("CreateNotificationsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private void sendNotification(UserEvent ue) {
        Notification.Builder notificationBuilder=new Notification.Builder(this).setSmallIcon(R.drawable.event_notification_icon).setContentTitle("Time to leave")
                .setContentText("Time to leave for "+ue.getName());//Moved this line down to aid code readability in Android Studio with my view settings
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,notificationBuilder.build());
    }
}
