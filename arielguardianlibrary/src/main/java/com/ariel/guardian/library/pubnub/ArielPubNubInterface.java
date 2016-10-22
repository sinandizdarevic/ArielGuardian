package com.ariel.guardian.library.pubnub;

import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielPubNubInterface {

    // pubnub methods
    void sendApplicationMessage(final String packageName, final String action);

    void sendLocationMessage(final long locationId, final String action);

    void sendConfigurationMessage(final long configID, final String action);

    void sendMessage(final Object commandMessage, final PNCallback<PNPublishResult> callback);

    void reconnect();

}
