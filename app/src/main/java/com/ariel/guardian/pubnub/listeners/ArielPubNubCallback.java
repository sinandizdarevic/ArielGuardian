package com.ariel.guardian.pubnub.listeners;

import android.util.Log;

import com.ariel.guardian.command.CommandListener;
import com.ariel.guardian.command.CommandProducer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import ariel.commands.ArielCommandMessage;

/**
 * Created by mikalackis on 4.7.16..
 */
public class ArielPubNubCallback extends SubscribeCallback {

    private final String TAG = "ArielPubNubCallback";

    static Gson gson = new GsonBuilder().create();

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        Log.i(this.getClass().getName(), "Status: " + status.getStatusCode());
        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
            // internet got lost, do some magic and call reconnect when ready
            pubnub.reconnect();
        } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
            // do some magic and call reconnect when ready
            pubnub.reconnect();
        } else {
            //log.error(status)
        }
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message){
        Log.i(TAG, "Received pubnub message: "+ message.getMessage().toString());
        ArielCommandMessage msg = gson.fromJson(message.getMessage().toString(), ArielCommandMessage.class);
        CommandListener command = CommandProducer.getInstance().getCommand(message.getSubscribedChannel(), msg.getAction());
        Log.i(TAG, "Received command from pubnub: "+msg.getAction()+" with params: "+msg.getParams());
        if(command!=null){
            Log.i(TAG, "Command not null");
            command.execute(msg.getParams());
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence){

    }
}
