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
public class PubNubService extends Service implements PubNubServiceInterface{

    private static final String TAG = "PubNubService";

    public static final String EXTRA_PUBNUB_CIPHER_KEY = "cipher_key";
    public static final String EXTRA_PUBNUB_SECRET_KEY = "secret_key";
    public static final String EXTRA_PUBNUB_PUBLISH_KEY = "publish_key";
    public static final String EXTRA_PUBNUB_SUBSCRIBE_KEY = "subscribe_key";

    private String mPubNubCipherKey;

    private String mPubNubSecretKey;

    private PubNubManager mPubNubManager;

    private boolean mIsRunning;

    private final IBinder mBinder = new PubNubServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating PubNubManager");

        if(!mIsRunning) {
            mPubNubManager = PubNubManager.getInstance(getApplicationContext());
            mIsRunning = true;
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void addSubscribeCallback(SubscribeCallback callback){
        Log.i(TAG, "Adding subscribe callback");
        mPubNubManager.addSubscribeCallback(callback);
    }

    @Override
    public void sendCommand(final CommandMessage commandMessage, final PNCallback<PNPublishResult> callback, final String... channels){
        Log.i(TAG, "Sending command");
        mPubNubManager.sendCommand(commandMessage,callback,channels);
    }

    @Override
    public void sendMessage(Object commandMessage, PNCallback<PNPublishResult> callback, String... channels) {
        Log.i(TAG, "Sending command");
        mPubNubManager.sendMessage(commandMessage, callback, channels);
    }

    @Override
    public void reconnect(){
        mPubNubManager.reconnect();
    }

    @Override
    public String getPubNubCipherKey(){
        return mPubNubCipherKey;
    }

    @Override
    public String getPubNubSecretKey(){
        return mPubNubSecretKey;
    }

    @Override
    public void subscribeToChannels(final String... channels){
        Log.i(TAG, "Subscribing to channels: "+channels);
        mPubNubManager.subscribeToChannels(channels);
    }

    @Override
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
        public PubNubServiceInterface getService() {
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
