package com.ariel.guardian.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.model.DeviceConfiguration;
import com.ariel.guardian.pubnub.PubNubManager;
import com.ariel.guardian.pubnub.listeners.ArielPubNubCallback;
import com.ariel.guardian.utils.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mikalackis on 2.7.16..
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
    public void onEvent(DeviceConfiguration deviceConfig) {
        mPubNubManager.init(deviceConfig.getPubNubPublishKey(), deviceConfig.getPubNubSubscribeKey(),deviceConfig.getPubNubSecretKey(),deviceConfig.getPubNubCipherKey());
        mPubNubManager.subscribeToChannels(Utilities.getPubNubConfigChannel(), Utilities.getPubNubLocationChannel(), Utilities.getPubNubApplicationChannel());
        mPubNubManager.addListener(new ArielPubNubCallback());
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
