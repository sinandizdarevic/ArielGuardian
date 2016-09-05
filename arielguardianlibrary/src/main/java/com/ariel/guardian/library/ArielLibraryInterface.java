package com.ariel.guardian.library;

import android.app.Application;
import android.content.Context;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 25.8.16..
 */
public interface ArielLibraryInterface {

    void destroy();

    void sendCommand(final CommandMessage command,
                     final PNCallback<PNPublishResult> callback,
                     final String... channels);

    void addPubNubSubscribeCallback(final SubscribeCallback callback);

    FirebaseHelper firebase();

    void subscribeToChannels(String... channels);

    void subscribeToChannelsWithCallback(SubscribeCallback callback, String... channels);

    void reconnect();

    Context getApplicationContenxt();

    DatabaseReference getApplicationReference(final String deviceId);

    DatabaseReference getLocationReference(final String deviceId);

    DatabaseReference getConfigurationReference(final String deviceId);

}
