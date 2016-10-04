package com.ariel.guardian.pubnub.listeners;

import android.util.Log;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.command.CommandProducer;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.receivers.NetworkChangeReceiver;
import com.ariel.guardian.pubnub.PubNubUtilities;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.JsonSyntaxException;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

/**
 * Created by mikalackis on 4.7.16..
 */
public class ArielPubNubCallback extends SubscribeCallback {

    private final String TAG = "ArielPubNubCallback";

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

    @Override
    public void message(PubNub pubnub, PNMessageResult message){
        Log.i(TAG, "Received pubnub message: "+ message.getMessage().toString()+" on channel: "+message.getSubscribedChannel());
        if(message == null || message.getMessage() == null){
            Log.i(TAG, "MESSAGE OBJECT IS NULL!!!");
            return;
        }

        try{
            CommandMessage msg = ArielUtilities.getGson().fromJson(message.getMessage().toString(), CommandMessage.class);
            Command command = CommandProducer.getInstance().getCommand(msg.getAction());
            Log.i(TAG, "Received command from pubnub: "+msg.getAction()+" with params: "+msg.getParams());
            if(command!=null){
                Log.i(TAG, "Command not null");
                command.execute(msg.getParams());
            }
        }
        catch(JsonSyntaxException exception){
            Log.e(TAG, "Invlid JSON structure!");
        }

    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {

    }

}
