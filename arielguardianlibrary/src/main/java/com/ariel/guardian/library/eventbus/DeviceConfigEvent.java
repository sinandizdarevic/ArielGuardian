package com.ariel.guardian.library.eventbus;

import com.ariel.guardian.library.model.DeviceConfiguration;
import com.pubnub.api.callbacks.SubscribeCallback;

/**
 * Created by mikalackis on 16.8.16..
 */
public class DeviceConfigEvent {

    private DeviceConfiguration mDeviceConfig;
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
