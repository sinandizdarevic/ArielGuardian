package com.ariel.guardian.library.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.library.eventbus.DeviceConfigEvent;
import com.ariel.guardian.library.model.DeviceConfiguration;
import com.ariel.guardian.library.pubnub.PubNubManager;
import com.ariel.guardian.library.utils.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Class responsible for managing PubNubManager instance. Runs in background and
 * reinitializes PubNub instance when EventBus delivers new DeviceConfiguration class
 */
public class PubNubService extends Service {

    private static final String TAG = "PubNubService";

    private PubNubManager mPubNubManager;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mPubNubManager = new PubNubManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating PubNubManager");

        return START_STICKY;
    }

    @Subscribe
    public void onEvent(final DeviceConfigEvent event) {
        DeviceConfiguration deviceConfiguration = event.getDeviceConfig();
        mPubNubManager.init(deviceConfiguration.getPubNubPublishKey(),
                deviceConfiguration.getPubNubSubscribeKey(),
                deviceConfiguration.getPubNubSecretKey(),
                deviceConfiguration.getPubNubCipherKey());
        mPubNubManager.subscribeToChannels(Utilities.getPubNubConfigChannel(),
                Utilities.getPubNubLocationChannel(), Utilities.getPubNubApplicationChannel());
        mPubNubManager.addListener(event.getCallback());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PubNubService destroyed");
        mPubNubManager.cleanUp();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
