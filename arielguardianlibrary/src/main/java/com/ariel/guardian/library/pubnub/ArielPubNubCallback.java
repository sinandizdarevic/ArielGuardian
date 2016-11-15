package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.library.utils.SharedPrefsManager;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.orhanobut.logger.Logger;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import io.realm.RealmObject;

/**
 * Created by mikalackis on 4.7.16..
 */
public abstract class ArielPubNubCallback extends SubscribeCallback {

    private final String TAG = "ArielPubNubCallback";

    private Gson mGson = ArielUtilities.getGson();

    private Context mContext;

    private ArielDatabase mDatabase;
    private ArielPubNub mPubNub;

    public ArielPubNubCallback(final Context context, final ArielDatabase database, final ArielPubNub pubnub) {
        this.mContext = context;
        this.mDatabase = database;
        this.mPubNub = pubnub;
    }

    protected abstract void pubnubConnected();

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Logger.i("Status: " + status.getStatusCode());
        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            // internet got lost, do some magic and call reconnect when ready
            Logger.i("Internet got lost");
        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
            // do some magic and call reconnect when ready
            Logger.i("TIMEOUT");
            //pubnub.reconnect();
        } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            Logger.i("YAY!!!! CONNECTED!!!");
            /**
             * Check WrapperMessage realm if there are messages to be sent
             */
            mPubNub.getMissedMessages();
            pubnubConnected();
            //log.error(status)
        } else {
            Logger.d( "SOMETHING!!!! HAPPENED0!!!: " + status.getStatusCode());
        }
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        Logger.i("Received pubnub message on channel: " + message.getSubscribedChannel());
        Logger.json(message.getMessage().toString());
        WrapperMessage currentMessage = mPubNub.parseIncomingMessage(message.getMessage().toString());
        SharedPrefsManager.getInstance(mContext).setLongPreferences(SharedPrefsManager.KEY_LAST_PUBNUB_MESSAGE, message.getTimetoken());
        if (currentMessage != null) {
            mPubNub.processPubNubMessage(currentMessage);
        } else {
            Logger.i("This was my message so ignore it");
            // my message or a parsing error, needs to be handled
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        Logger.i("Presence of " + presence.getUuid());
    }

}
