package com.ariel.guardian.library.pubnub;

import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielPubNubInterface {

    // pubnub methods
    long createApplicationMessage(final DeviceApplication application, final String action, final boolean reportReception);

    long createLocationMessage(final long locationId, final String action, final boolean reportReception);

    long createConfigurationMessage(final long configID, final String action, final boolean reportReception);

    long createWrapperMessage(final WrapperMessage message);

    void sendMessage(final Object commandMessage, final PNCallback<PNPublishResult> callback);

    void reconnect();

    void subscribeToChannels(final String... channels);

    void subscribeToChannelsFromDB();

    void addSubscribeCallback(final SubscribeCallback callback);

}
