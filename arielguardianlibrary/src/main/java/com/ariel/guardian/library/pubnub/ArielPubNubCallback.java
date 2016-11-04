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
import com.google.gson.Gson;
import com.google.zxing.WriterException;
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

    protected abstract void messageProcessed(long messageID);

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.i(TAG,"Status: " + status.getStatusCode());
        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            // internet got lost, do some magic and call reconnect when ready
            Log.i(TAG,"Internet got lost");
        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
            // do some magic and call reconnect when ready
            Log.i(TAG,"TIMEOUT");
            //pubnub.reconnect();
        } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
            Log.i(TAG,"YAY!!!! CONNECTED!!!");
            /**
             * Check WrapperMessage realm if there are messages to be sent
             */
            pubnubConnected();
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
        Log.i(ArielDatabase.TAG,"Received pubnub message: " + message.getMessage().toString() + " on channel: " + message.getSubscribedChannel());
        WrapperMessage currentMessage = parseIncomingMessage(message.getMessage().toString());

        if (currentMessage != null) {
            // its not a message from me, deal with it

            boolean isArielDevice = false;

            // check if the sender is actually an ArielDevice
            ArielDevice arielDevice = mDatabase.getDeviceByID(currentMessage.getSender());
            // if it is ArielDevice, it is some type of report
            if (arielDevice != null) {
                Log.i(ArielDatabase.TAG,"This message came from an ArielDevice!!");
                // check if we have a wrapper message of this id locally
                // remove the local one so that we dont execute the same command again
                isArielDevice = true;
            }
            else{
                Log.i(ArielDatabase.TAG,"This message is not an ArielDevice");
            }

            Object dataToTransfer = null;

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
            } else if(currentMessage.getActionType().equals(ArielConstants.TYPE_GET_DEVICE_QR_CODE)){
                try {
                    Bitmap qr_code = ArielUtilities.generateDeviceQRCode(ArielUtilities.getUniquePseudoID());
                    String base64bitmap = ArielUtilities.base64Encode2String(qr_code);

                    dataToTransfer = base64bitmap;
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            } else if(currentMessage.getActionType().equals(ArielConstants.TYPE_WRAPPER_MESSAGE_REPORT)){
                // just a report message, nothing to do except for deleting it
                Log.i(ArielDatabase.TAG, "This is a report message!");
            }

            if (currentMessage.getReportReception()) {
                Log.i(ArielDatabase.TAG,"This message requires report reception");
                currentMessage.setDataObject(mGson.toJson(dataToTransfer));
                currentMessage.setReportReception(false);
                currentMessage.setSender(ArielUtilities.getUniquePseudoID());
                currentMessage.setActionType(ArielConstants.TYPE_WRAPPER_MESSAGE_REPORT);
                long id = mPubNub.createWrapperMessage(currentMessage);
                messageProcessed(id);
            }
            else{
                Log.i(ArielDatabase.TAG,"This message does not require report reception");
                mDatabase.deleteWrapperMessageByID(currentMessage.getId());
            }
        } else {
            Log.i(ArielDatabase.TAG,"This was my message so ignore it");
            // my message or a parsing error, needs to be handled
        }

    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }

}
