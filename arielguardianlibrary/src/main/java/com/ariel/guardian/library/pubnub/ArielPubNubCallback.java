package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.ArielDatabase;
import com.ariel.guardian.library.db.model.ArielDevice;
import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.ariel.guardian.library.receiver.NetworkChangeReceiver;
import com.ariel.guardian.library.services.SyncIntentService;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import io.realm.RealmObject;

/**
 * Created by mikalackis on 4.7.16..
 */
public class ArielPubNubCallback extends SubscribeCallback {

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

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.i(TAG,"Status: " + status.getStatusCode());
        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            // internet got lost, do some magic and call reconnect when ready
            Log.i(TAG,"Internet got lost");
            PubNubUtilities.getInstance().registerNetworkListener();
        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
            // do some magic and call reconnect when ready
            Log.i(TAG,"TIMEOUT");
            //pubnub.reconnect();
        } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            Log.i(TAG,"YAY!!!! CONNECTED!!!");
            /**
             * Check WrapperMessage realm if there are messages to be sent
             */
            mContext.startService(SyncIntentService.getSyncIntent(-1));
            //log.error(status)
        } else {
            Log.i(TAG,"SOMETHING!!!! HAPPENED0!!!: " + status.getStatusCode());
        }
    }

    protected WrapperMessage parseIncomingMessage(String message) {
        WrapperMessage msg = mGson.fromJson(message, WrapperMessage.class);
        if (msg != null) {
            if (msg.getSender().equals(ArielUtilities.getUniquePseudoID())) {
                //this is my message
                return null;
            } else {
                // this one is OK, pass it on
                return msg;
            }
        }
        return msg;
    }


    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        Log.i(TAG,"Received pubnub message: " + message.getMessage().toString() + " on channel: " + message.getSubscribedChannel());
        WrapperMessage currentMessage = parseIncomingMessage(message.getMessage().toString());

        if (currentMessage != null) {
            // its not a message from me, deal with it

            // check if the sender is actually an ArielDevice
            ArielDevice arielDevice = mDatabase.getDeviceByID(currentMessage.getSender());
            // if it is ArielDevice, it is some type of report
            if (arielDevice != null) {
                Log.i(TAG,"This message came from an ArielDevice!!");
                // check if we have a wrapper message of this id locally
                // remove the local one so that we dont execute the same command again
                Log.i(TAG,"Trying to remove wrapper message with id: "+currentMessage.getId());
                mDatabase.deleteWrapperMessageByID(currentMessage.getId());
            }

            RealmObject dataToTransfer = null;

            // now deal with it
            if (currentMessage.getActionType().equals(ArielConstants.TYPE_APPLICATION_ADDED)) {
                // add an app to the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                mDatabase.createOrUpdateApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_ADDED);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
                mContext.sendBroadcast(appIntent);

                dataToTransfer = deviceApp;

            }
            if (currentMessage.getActionType().equals(ArielConstants.TYPE_APPLICATION_UPDATED)) {
                // add an app to the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                mDatabase.createOrUpdateApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_UPDATED);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
                mContext.sendBroadcast(appIntent);

                dataToTransfer = deviceApp;
            } else if (currentMessage.getActionType().equals(ArielConstants.TYPE_APPLICATION_REMOVED)) {
                // remove an app from the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                mDatabase.deleteApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_REMOVED);
                mContext.sendBroadcast(appIntent);

                dataToTransfer = deviceApp;
            } else if (currentMessage.getActionType().equals(ArielConstants.TYPE_DEVICE_CONFIG_UPDATE)) {
                // update device configuration and broadcast change
                Configuration deviceConfig = mGson.fromJson(currentMessage.getDataObject(), Configuration.class);
                mDatabase.createConfiguration(deviceConfig);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_DEVICE_CONFIG_UPDATE);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceConfig.getId());
                mContext.sendBroadcast(appIntent);

                dataToTransfer = deviceConfig;
            } else if (currentMessage.getActionType().equals(ArielConstants.TYPE_LOCATION_UPDATE)) {
                // update device location and broadcast change
                DeviceLocation deviceLocation = mGson.fromJson(currentMessage.getDataObject(), DeviceLocation.class);
                mDatabase.createLocation(deviceLocation);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_LOCATION_UPDATE);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceLocation.getId());
                mContext.sendBroadcast(appIntent);

                dataToTransfer = deviceLocation;
            }

            if (currentMessage.getReportReception()) {
                // send the message with the new object
                currentMessage.setDataObject(mGson.toJson(dataToTransfer));
                currentMessage.setReportReception(false);
                currentMessage.setSender(ArielUtilities.getUniquePseudoID());
                mPubNub.sendWrapperMessage(currentMessage);
            }
        } else {
            Log.i(TAG,"This was my message so ignore it");
            // my message or a parsing error, needs to be handled
        }

    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }

}
