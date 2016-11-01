package com.ariel.guardian.library.pubnub;

import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielPubNubInterface {

    // pubnub methods
    void sendApplicationMessage(final DeviceApplication application, final String action, final boolean reportReception);

    void sendLocationMessage(final long locationId, final String action, final boolean reportReception);

    void sendWrapperMessage(final WrapperMessage message);

    void sendConfigurationMessage(final long configID, final String action, final boolean reportReception);

    void sendMessage(final Object commandMessage, final PNCallback<PNPublishResult> callback);

    void reconnect();

    void subscribeToChannels(final String... channels);

    void subscribeToChannelsFromDB();

}
