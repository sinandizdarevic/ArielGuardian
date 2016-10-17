package com.ariel.guardian.library.pubnub;

import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielPubNubInterface {

    // pubnub methods
    void sendApplicationMessage(final long appId, final String action);

    void sendMessage(final Object commandMessage, final PNCallback<PNPublishResult> callback);

    void reconnect();

}
