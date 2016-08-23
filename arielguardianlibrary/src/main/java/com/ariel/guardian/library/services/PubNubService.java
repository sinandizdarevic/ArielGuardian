package com.ariel.guardian.library.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.model.DeviceConfiguration;
import com.ariel.guardian.library.pubnub.PubNubManager;
import com.ariel.guardian.library.utils.Utilities;
import com.pubnub.api.callbacks.SubscribeCallback;

/**
 * Class responsible for managing PubNubManager instance. Runs in background and
 * reinitializes PubNub instance when EventBus delivers new DeviceConfiguration class
 */
public class PubNubService extends Service {

    private static final String TAG = "PubNubService";

    private PubNubManager mPubNubManager;

    private final IBinder mBinder = new PubNubServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mPubNubManager = new PubNubManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating PubNubManager");

        return START_STICKY;
    }

//    @Subscribe
//    public void onEvent(final DeviceConfigEvent event) {
//        DeviceConfiguration deviceConfiguration = event.getDeviceConfig();
//        mPubNubManager.init(deviceConfiguration.getPubNubPublishKey(),
//                deviceConfiguration.getPubNubSubscribeKey(),
//                deviceConfiguration.getPubNubSecretKey(),
//                deviceConfiguration.getPubNubCipherKey());
//        mPubNubManager.subscribeToChannels(Utilities.getPubNubConfigChannel(),
//                Utilities.getPubNubLocationChannel(), Utilities.getPubNubApplicationChannel());
//        mPubNubManager.addListener(event.getCallback());
//        //SharedPrefsManager.getInstance(getApplicationContext()).savePubNubData(deviceConfiguration);
//    }

    public void initPubNub(final DeviceConfiguration deviceConfiguration, final SubscribeCallback callback){
        mPubNubManager.init(deviceConfiguration.getPubNubPublishKey(),
                deviceConfiguration.getPubNubSubscribeKey(),
                deviceConfiguration.getPubNubSecretKey(),
                deviceConfiguration.getPubNubCipherKey());
        mPubNubManager.subscribeToChannels(Utilities.getPubNubConfigChannel(),
                Utilities.getPubNubLocationChannel(), Utilities.getPubNubApplicationChannel());
        mPubNubManager.addListener(callback);
    }

    public void sendCommand(final CommandMessage command, final String channel){
        mPubNubManager.sendCommand(command, channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PubNubService destroyed");
        mPubNubManager.cleanUp();
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
