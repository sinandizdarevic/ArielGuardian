package com.ariel.guardian.library.firebase;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mikalackis on 23.5.16..
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//        Intent i = new Intent(ArielGuardianApplication.getInstance(), DeviceConfigService.class);
//        ArielGuardianApplication.getInstance().startService(i);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.i(TAG, "MESSAGE BODY: "+messageBody);
    }

}
