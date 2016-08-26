package com.ariel.guardian.library;

import android.app.Application;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 25.8.16..
 */
public interface ArielLibraryInterface {

    void destroy();

    void sendCommand(final CommandMessage command, final String channel,
                     final PNCallback<PNPublishResult> callback);

    void addPubNubSubscribeCallback(final SubscribeCallback callback);

    FirebaseHelper firebase();

}
