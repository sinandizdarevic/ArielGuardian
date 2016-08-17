package com.ariel.guardian.library.eventbus;

import com.ariel.guardian.library.model.DeviceConfiguration;
import com.pubnub.api.callbacks.SubscribeCallback;

/**
 * Class used by EventBus to inform listeners about new device configuration event.
 */
public class DeviceConfigEvent {

    /**
     * New device configuration
     */
    private DeviceConfiguration mDeviceConfig;
    /**
     * PubNub subscription callback since device configuration contains
     * PubNub keys
     */
    private SubscribeCallback mCallback;

    public DeviceConfigEvent(final DeviceConfiguration deviceConfiguration, final SubscribeCallback callback){
        this.mDeviceConfig=deviceConfiguration;
        this.mCallback = callback;
    }

    public DeviceConfiguration getDeviceConfig() {
        return mDeviceConfig;
    }

    public SubscribeCallback getCallback() {
        return mCallback;
    }
}
