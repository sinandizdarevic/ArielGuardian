package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.ariel.guardian.library.receiver.NetworkChangeReceiver;
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

/**
 * Created by mikalackis on 4.7.16..
 */
public class ArielPubNubCallback extends SubscribeCallback {

    private final String TAG = "ArielPubNubCallback";

    private NetworkChangeReceiver mNetworkListener = new NetworkChangeReceiver();

    private Gson mGson = ArielUtilities.getGson();

    private Context mContext;

    public ArielPubNubCallback(final Context context){
        this.mContext = context;
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.i(TAG, "Status: " + status.getStatusCode());
        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            // internet got lost, do some magic and call reconnect when ready
            Log.i(TAG, "Internet got lost");
            PubNubUtilities.getInstance().registerNetworkListener();
        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
            // do some magic and call reconnect when ready
            Log.i(TAG, "TIMEOUT");
            //pubnub.reconnect();
        } else if(status.getCategory() == PNStatusCategory.PNConnectedCategory){
            Log.i(TAG, "YAY!!!! CONNECTED!!!");
            /**
             * Check WrapperMessage realm if there are messages to be sent
             */
            //log.error(status)
        }
        else{
            Log.i(TAG, "SOMETHING!!!! HAPPENED0!!!: "+status.getStatusCode());
        }
    }

    protected WrapperMessage parseIncomingMessage(String message){
        WrapperMessage msg = mGson.fromJson(message, WrapperMessage.class);
        if(msg!=null){
            if(msg.getSender().equals(ArielUtilities.getUniquePseudoID())){
                //this is my message
                return null;
            }
            else{
                // this one is OK, pass it on
                return msg;
            }
        }
        return msg;
    }


    @Override
    public void message(PubNub pubnub, PNMessageResult message){
        Log.i(TAG, "Received pubnub message: "+ message.getMessage().toString()+" on channel: "+message.getSubscribedChannel());
        WrapperMessage currentMessage = parseIncomingMessage(message.getMessage().toString());

        if(currentMessage!=null){
            // its not a message from me, send it back to confirm reception
            PubNubManager.getInstance(mContext).sendMessage(currentMessage, new PNCallback<PNPublishResult>() {
                @Override
                public void onResponse(PNPublishResult result, PNStatus status) {

                }
            });

            // now deal with it
            if(currentMessage.getType().equals(ArielConstants.TYPE_APPLICATION_ADDED)){
                // add an app to the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                Ariel.action().database().createOrUpdateApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_ADDED);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
                mContext.sendBroadcast(appIntent);
            }
            if(currentMessage.getType().equals(ArielConstants.TYPE_APPLICATION_UPDATED)){
                // add an app to the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                Ariel.action().database().createOrUpdateApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_UPDATED);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
                mContext.sendBroadcast(appIntent);
            }
            else if(currentMessage.getType().equals(ArielConstants.TYPE_APPLICATION_REMOVED)){
                // remove an app from the realm database
                DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
                Ariel.action().database().deleteApplication(deviceApp);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_APPLICATION_REMOVED);
                mContext.sendBroadcast(appIntent);
            }
            else if(currentMessage.getType().equals(ArielConstants.TYPE_DEVICE_CONFIG_UPDATE)){
                // update device configuration and broadcast change
                Configuration deviceConfig = mGson.fromJson(currentMessage.getDataObject(), Configuration.class);
                Ariel.action().database().createConfiguration(deviceConfig);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_DEVICE_CONFIG_UPDATE);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceConfig.getId());
                mContext.sendBroadcast(appIntent);
            }
            else if(currentMessage.getType().equals(ArielConstants.TYPE_LOCATION_UPDATE)){
                // update device location and broadcast change
                DeviceLocation deviceLocation = mGson.fromJson(currentMessage.getDataObject(), DeviceLocation.class);
                Ariel.action().database().createLocation(deviceLocation);
                Intent appIntent = new Intent();
                appIntent.setAction(ArielConstants.TYPE_LOCATION_UPDATE);
                appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceLocation.getId());
                mContext.sendBroadcast(appIntent);
            }
        }
        else{
            Log.i(TAG, "This was my message so ignore it");
            // my message or a parsing error, needs to be handled
        }

    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }

}
