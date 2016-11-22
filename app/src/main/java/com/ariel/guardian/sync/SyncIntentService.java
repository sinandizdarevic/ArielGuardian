package com.ariel.guardian.sync;

import android.app.IntentService;
import android.content.Intent;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.orhanobut.logger.Logger;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mikalackis on 7.10.16..
 */

public class SyncIntentService extends IntentService {

    public static final String TAG = "SyncIntentService";

    private static String EXTRA_MESSAGE_ID = "message_id";

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Override
    public void onCreate() {
        super.onCreate();
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * 1. Retrieve ID fron intent and application object from realm
         * 2. Ship it via pubnub
         */
        Logger.d( "Received intent action: " + intent.getAction());

        final long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
        if (messageId != -1) {
            final WrapperMessage message = mArielDatabase.getWrapperMessageByID(messageId);
            Logger.d(ArielDatabase.TAG, "DATA TO SEND: " + ArielUtilities.getGson().toJson(message));
            sendMessage(message);
        } else {
            // we need to check all the wrapper messages and send them out
            List<WrapperMessage> wrapperMessages = mArielDatabase.getUnsentWrapperMessages();
            if (wrapperMessages != null && wrapperMessages.size() > 0) {
                Iterator<WrapperMessage> itMessages = wrapperMessages.iterator();
                while (itMessages.hasNext()) {
                    final WrapperMessage message = itMessages.next();
                    Logger.d(ArielDatabase.TAG, "Sending leftover message: " + ArielUtilities.getGson().toJson(message));
                    sendMessage(message);
                }
            }
        }

    }

    private void sendMessage(final WrapperMessage message) {
        mArielPubNub.sendMessage(message, new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (!status.isError()) {
                    // everything is ok, remove wrapper message from realm
                    if (!message.getReportReception()) {
                        Logger.i(TAG, "Message sent, remove wrapper");
                        message.setSent(true);
                        mArielDatabase.deleteWrapperMessage(message);
                    } else {
                        Logger.i(TAG, "Waiting for execution feedback for id: " + message.getId());
                        message.setSent(true);
                        mArielDatabase.createWrapperMessage(message);
                    }
                } else {
                    // keep trying until you send the message
                    // this should probably be replaced with some advanced mechanism
                    Logger.d(ArielDatabase.TAG, "Status is error: " + status.isError());
                    Logger.d(ArielDatabase.TAG, "Status error details: " + status.getErrorData().getInformation());
                    Logger.d(ArielDatabase.TAG, "Status error exception: " + status.getErrorData().getThrowable().getMessage());
                    Logger.d(ArielDatabase.TAG, "Status code: " + status.getStatusCode());
                    Logger.d(ArielDatabase.TAG, "Message not sent, retry??");
                    message.setSent(false);
                    mArielDatabase.createWrapperMessage(message);
                    status.retry();
                }
            }
        });
    }

    public static Intent getSyncIntent(final long messageId) {
        Intent appIntent = new Intent(GuardianApplication.getInstance(), SyncIntentService.class);
        appIntent.putExtra(EXTRA_MESSAGE_ID, messageId);
        return appIntent;
    }
}