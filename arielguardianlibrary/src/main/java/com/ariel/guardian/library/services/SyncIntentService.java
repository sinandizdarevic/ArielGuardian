package com.ariel.guardian.library.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mikalackis on 7.10.16..
 */

public class SyncIntentService extends IntentService {

    public static final String TAG = "SyncIntentService";

    private static String EXTRA_MESSAGE_ID = "message_id";

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * 1. Retrieve ID fron intent and application object from realm
         * 2. Ship it via pubnub
         */
        Log.i(TAG, "Received intent action: " + intent.getAction());

        final long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
        if (messageId != -1) {
            final WrapperMessage message = Ariel.action().database().getWrapperMessageByID(messageId);
            Log.i(TAG, "DATA TO SEND: " + ArielUtilities.getGson().toJson(message));
            sendMessage(message);
        } else {
            // we need to check all the wrapper messages and send them out
            List<WrapperMessage> wrapperMessages = Ariel.action().database().getUnsentWrapperMessages();
            if (wrapperMessages != null && wrapperMessages.size() > 0) {
                Iterator<WrapperMessage> itMessages = wrapperMessages.iterator();
                while (itMessages.hasNext()) {
                    final WrapperMessage message = itMessages.next();
                    Log.i(TAG, "Sending leftover message: " + ArielUtilities.getGson().toJson(message));
                    sendMessage(message);
                }
            }
        }

    }

    private void sendMessage(final WrapperMessage message) {
        Ariel.action().pubnub().sendMessage(message, new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (!status.isError()) {
                    // everything is ok, remove wrapper message from realm
                    if (!message.getReportReception()) {
                        Log.i(TAG, "Message sent, remove wrapper");
                        Ariel.action().database().deleteWrapperMessage(message);
                    } else {
                        Log.i(TAG, "Waiting for execution feedback for id: " + message.getId());
                        message.setSent(true);
                        Ariel.action().database().createWrapperMessage(message);
                    }
                } else {
                    // keep trying until you send the message
                    // this should probably be replaced with some advanced mechanism
                    Log.i(TAG, "Status is error: " + status.isError());
                    Log.i(TAG, "Status error details: " + status.getErrorData().getInformation());
                    Log.i(TAG, "Status error exception: " + status.getErrorData().getThrowable().getMessage());
                    Log.i(TAG, "Status code: " + status.getStatusCode());
                    Log.i(TAG, "Message not sent, retry??");
                    message.setSent(false);
                    Ariel.action().database().createWrapperMessage(message);
                    status.retry();
                }
            }
        });
    }

    public static Intent getSyncIntent(final long messageId) {
        Intent appIntent = new Intent(Ariel.getMyApplicationContext(), SyncIntentService.class);
        appIntent.putExtra(EXTRA_MESSAGE_ID, messageId);
        return appIntent;
    }
}