package com.ariel.guardian.library;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.pubnub.PubNubService;
import com.ariel.guardian.library.utils.Utilities;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

/**
 * Ariel library main entry point.
 */
public final class ArielLibrary implements ArielLibraryInterface{

    private static final String TAG = "ArielLibrary";

    private static boolean mPrepared;
    private static FirebaseHelper mFireBaseHelper;

    private static PubNubService mPubNubService;

    private static boolean mPubNubServiceBound;

    private static Application mApplication;

    private static ArielLibraryInterface mInstance;

    private static SubscribeCallback mCallback;

    private ArielLibrary(){
        // prevent this class from being instantiable
    }

    /**
     * Performs library initialization
     *
     * @param Application
     */
    public static void prepare(final Application application, final String deviceId, final SubscribeCallback callback) {

        if(!mPrepared) {

            mInstance = new ArielLibrary();

            Utilities.setDeviceId(deviceId);

            mApplication = application;

            mCallback = callback;

            // starts pubnub service
            Intent pubNubService = new Intent(application, PubNubService.class);
            pubNubService.putExtra(PubNubService.PARAM_DEVICE_ID, deviceId);
            application.startService(pubNubService);

            // init firebase
            mFireBaseHelper = new FirebaseHelper();

            // bind to pubnubservice
            application.bindService(pubNubService, mConnection, Context.BIND_AUTO_CREATE);

            mPrepared = true;

        }
    }

    @Override
    public void destroy(){
        mApplication.unbindService(mConnection);
    }

    public static ArielLibraryInterface action(){
        if(!mPrepared){
            throw new RuntimeException("Library not prepared! Call prepare() first!!!");
        }
        return mInstance;
    }

    @Override
    public void sendCommand(final CommandMessage command, final String channel,
                            final PNCallback<PNPublishResult> callback){
        if(mPubNubServiceBound){
            mPubNubService.sendCommand(command, channel, callback);
        }
    }

    @Override
    public void addPubNubSubscribeCallback(final SubscribeCallback callback){
        Log.i(TAG, "Adding callback");
        if(mPubNubServiceBound){
            Log.i(TAG, "Adding callback, service bound");
            mPubNubService.addSubscribeCallback(callback);
        }
    }

    @Override
    public FirebaseHelper firebase(){
        return mFireBaseHelper;
    }

    private static ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service bound!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) service;
            mPubNubService = binder.getService();
            mPubNubService.addSubscribeCallback(mCallback);
            mPubNubServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPubNubServiceBound = false;
        }
    };

}
