package com.ariel.guardian.library.pubnub;

import com.ariel.guardian.library.commands.CommandMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 7.9.16..
 */
public interface PubNubServiceInterface {

    void addSubscribeCallback(SubscribeCallback callback);

    void sendMessage(final Object commandMessage, final PNCallback<PNPublishResult> callback, final String... channels);

     void reconnect();

     void subscribeToChannels(final String... channels);

     void subscribeToChannelsWithCallback(SubscribeCallback callback, final String... channels);

}
