package com.ariel.guardian.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.firebase.model.DeviceConfiguration;
import com.ariel.guardian.library.pubnub.PubNubManager;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import javax.inject.Inject;

/**
 * Class responsible for managing PubNubManager instance. Runs in background and
 * reinitializes PubNub instance when EventBus delivers new DeviceConfiguration class
 */
public class PubNubService extends Service {

    private static final String TAG = "PubNubService";

    @Inject
    PubNubManager pubNubManager;

    private final IBinder mBinder = new PubNubServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating PubNubManager");

        return START_STICKY;
    }

    public void initPubNub(final DeviceConfiguration deviceConfiguration, final SubscribeCallback callback){
        pubNubManager.init(deviceConfiguration.getPubNubPublishKey(),
                deviceConfiguration.getPubNubSubscribeKey(),
                deviceConfiguration.getPubNubSecretKey(),
                deviceConfiguration.getPubNubCipherKey(), callback);
    }

    public void sendCommand(final CommandMessage command, final String channel, final PNCallback<PNPublishResult> callback){
        pubNubManager.sendCommand(command, channel, callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PubNubService destroyed");
        pubNubManager.cleanUp();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PubNubServiceBinder extends Binder {
        public PubNubService getService() {
            // Return this instance of PubNubService so clients can call public methods
            return PubNubService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
