package com.ariel.guardian.firebase;

import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.FirebaseDeviceConfigService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mikalackis on 23.5.16..
 */
public class ArielFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = ArielFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Intent i = new Intent(ArielGuardianApplication.getInstance(), FirebaseDeviceConfigService.class);
        ArielGuardianApplication.getInstance().startService(i);
    }

}
