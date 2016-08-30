package com.ariel.guardian.library.pubnub;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Class responsible for managing PubNubManager instance. Runs in background and
 * reinitializes PubNub instance when EventBus delivers new DeviceConfiguration class
 */
public class PubNubService extends Service {

    private static final String TAG = "PubNubService";

    private PubNubManager mPubNubManager;

    private boolean mIsRunning;

    private final IBinder mBinder = new PubNubServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating PubNubManager");

        if(!mIsRunning) {
            mPubNubManager = new PubNubManager(getApplicationContext());
            mIsRunning = true;
        }

        return START_STICKY;
    }

    public void addSubscribeCallback(SubscribeCallback callback){
        Log.i(TAG, "Adding subscribe callback");
        mPubNubManager.addSubscribeCallback(callback);
    }

    public void sendCommand(final CommandMessage commandMessage, final PNCallback<PNPublishResult> callback, final String... channels){
        Log.i(TAG, "Sending command");
        mPubNubManager.sendCommand(commandMessage,callback,channels);
    }

    public void subscribeToChannels(final String... channels){
        Log.i(TAG, "Subscribing to channels: "+channels);
        mPubNubManager.subscribeToChannels(channels);
    }

    public void subscribeToChannelsWithCallback(SubscribeCallback callback, final String... channels){
        Log.i(TAG, "Subscribing to channels: "+channels);
        addSubscribeCallback(callback);
        mPubNubManager.subscribeToChannels(channels);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PubNubService destroyed");
        mPubNubManager.cleanUp();
        mIsRunning = false;
    }

    public class PubNubServiceBinder extends Binder {
        public PubNubService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PubNubService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
