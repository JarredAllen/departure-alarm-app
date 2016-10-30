package com.example.jarred.departurealarm;

import android.app.IntentService;
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
}
