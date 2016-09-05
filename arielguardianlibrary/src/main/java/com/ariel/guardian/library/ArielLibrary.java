package com.ariel.guardian.library;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.pubnub.PubNubService;
import com.google.firebase.database.DatabaseReference;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

import java.lang.ref.WeakReference;

/**
 * Ariel library main entry point.
 */
public final class ArielLibrary implements ArielLibraryInterface {

    private static final String TAG = "ArielLibrary";

    public static final String BROADCAST_LIBRARY_READY = "ariel_library_ready";

    private static boolean mPrepared;
    private static FirebaseHelper mFireBaseHelper;

    private static PubNubService mPubNubService;

    private static boolean mPubNubServiceBound;

    private static WeakReference<Application> mApplication;

    private static ArielLibraryInterface mInstance;

    private ArielLibrary(final Application application) {
        // prevent this class from being instantiable
        mApplication = new WeakReference<Application>(application);

        // starts pubnub service
        Intent pubNubService = new Intent(application, PubNubService.class);
        application.startService(pubNubService);

        // init firebase
        mFireBaseHelper = new FirebaseHelper();

        // bind to pubnubservice
        application.bindService(pubNubService, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *
     * Performs library initialization
     *
     * @param Application
     */
    public static void prepare(final Application application) {
        if (!mPrepared) {
            mInstance = new ArielLibrary(application);
            mPrepared = true;
        }
    }

    @Override
    public void destroy() {
        if(mApplication.get()!=null) {
            mApplication.get().unbindService(mConnection);
        }
    }

    public static ArielLibraryInterface action() {
        if (!mPrepared || !mPubNubServiceBound) {
            throw new RuntimeException("Library not prepared! Call prepare() first!!!");
        }
        return mInstance;
    }

    public static boolean prepared(){
        return mPrepared && mPubNubServiceBound;
    }

    @Override
    public void sendCommand(final CommandMessage command,
                            final PNCallback<PNPublishResult> callback, final String... channels) {
        mPubNubService.sendCommand(command, callback, channels);
    }

    @Override
    public void addPubNubSubscribeCallback(final SubscribeCallback callback) {
        Log.i(TAG, "Adding callback");
        mPubNubService.addSubscribeCallback(callback);
    }

    @Override
    public FirebaseHelper firebase() {
        return mFireBaseHelper;
    }

    @Override
    public void subscribeToChannels(String... channels) {
        mPubNubService.subscribeToChannels(channels);
    }

    @Override
    public void subscribeToChannelsWithCallback(SubscribeCallback callback, String... channels) {
        mPubNubService.subscribeToChannelsWithCallback(callback, channels);
    }

    @Override
    public void reconnect() {
        mPubNubService.reconnect();
    }

    @Override
    public Context getApplicationContenxt() {
        if(mApplication.get()!=null){
            return mApplication.get().getApplicationContext();
        }
        return null;
    }

    @Override
    public DatabaseReference getApplicationReference(final String deviceId) {
        return mFireBaseHelper.getApplicationReference(deviceId);
    }

    @Override
    public DatabaseReference getLocationReference(final String deviceId) {
        return mFireBaseHelper.getLocationReference(deviceId);
    }

    @Override
    public DatabaseReference getConfigurationReference(String deviceId) {
        return mFireBaseHelper.getConfigurationReference(deviceId);
    }

    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service bound!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) service;
            mPubNubService = binder.getService();
            mPubNubServiceBound = true;

            if(mApplication.get()!=null) {
                Intent intent = new Intent(BROADCAST_LIBRARY_READY);
                LocalBroadcastManager.getInstance(mApplication.get()).sendBroadcast(intent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPubNubServiceBound = false;
        }
    };

}
