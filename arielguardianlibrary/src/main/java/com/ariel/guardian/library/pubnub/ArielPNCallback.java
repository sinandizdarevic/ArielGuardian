package com.ariel.guardian.library.pubnub;

import android.util.Log;

import com.ariel.guardian.library.receivers.NetworkChangeReceiver;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * Created by mikalackis on 4.7.16..
 */
public abstract class ArielPNCallback extends SubscribeCallback {

    private final String TAG = "ArielPNCallback";

    private NetworkChangeReceiver mNetworkListener = new NetworkChangeReceiver();

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
            //log.error(status)
        }
        else{
            Log.i(TAG, "SOMETHING!!!! HAPPENED0!!!: "+status.getStatusCode());
        }
    }

    public abstract void message(PubNub pubnub, PNMessageResult message);
    public abstract void presence(PubNub pubnub, PNPresenceEventResult presence);

}
