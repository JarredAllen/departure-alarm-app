package com.example.jarred.departurealarm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.Settings;

import java.util.Collection;
import java.util.List;

/**
 * The service that handles notifying the user of things that are going on in the background.
 *
 * @author Jarred
 * @version 10/31/2016
 */
public class CreateNotificationsService extends IntentService {

    private static final String packageName="com.example.jarred.departurealarm";

    public CreateNotificationsService() {
        super("CreateNotificationsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Collection<UserEvent> events= DatabaseRetriever.getEvents();
        DatabaseRetriever.setLastLoadTime(""+ System.currentTimeMillis());
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException ie) {
            assert true;
        }
        for(UserEvent ue:events) {
            sendNotification(ue);
        }
    }

    private void sendNotification(UserEvent ue, EventNotification en) {
        if(ue.getNotifications().contains(en)) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent=new Intent(this, DeliverNotifications.class);

            intent.putExtra(packageName+".eventName", ue.getName());
            intent.putExtra(packageName+".loadTIme", ""+System.currentTimeMillis());

            PendingIntent pi=PendingIntent.getService(this,0,intent, PendingIntent.FLAG_ONE_SHOT);

            am.set(AlarmManager.RTC_WAKEUP, ue.getTime() * 1000L - DatabaseRetriever.getTravelTime(ue) * 1000L, pi);
        }
        else {
            throw new IllegalStateException("ue does not have that event notification");
        }
    }

    private void sendNotification(UserEvent ue) {
        for(EventNotification en:ue.getNotifications()) {
            sendNotification(ue, en);
        }
    }
}
