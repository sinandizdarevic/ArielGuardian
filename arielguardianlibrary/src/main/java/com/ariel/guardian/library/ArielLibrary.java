package com.ariel.guardian.library;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.library.pubnub.PubNubService;
import com.pubnub.api.callbacks.SubscribeCallback;

/**
 * Ariel library main entry point.
 */
public class ArielLibrary {

    /**
     * Performs library initialization
     *
     * @param Application
     */
    public static void prepare(final Application application, final SubscribeCallback callback) {
        // starts pubnub service
        Intent pubNubService = new Intent(application, PubNubService.class);
        application.startService(pubNubService);

        if(callback!=null){
            Intent intent = new Intent(application, PubNubService.class);
            application.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Log.i("ArielLibrary", "Bound to PubNubService");
                    PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) iBinder;
                    PubNubService service = binder.getService();
                    service.addSubscribeCallback(callback);
                    application.unbindService(this);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.i("ArielLibrary", "Unbound from PubNubService");
                }
            }, Context.BIND_AUTO_CREATE);
        }
    }

}
