package com.ariel.guardian.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

/**
 * Class responsible for managing PubNubManager instance.
 */
public class InstanceKeeperService extends Service{

    private static final String TAG = "InstanceKeeperService";

    private ArielPubNub mArielPubnub;

    private boolean mIsRunning;

    @Inject
    ArielPubNub mArielPubNub;

    @Inject
    ArielDatabase mArielDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d( "Initating Instances");

        if(!mIsRunning) {
            Logger.d("InstanceKeeper not running");
            mArielPubNub.reconnect();
            mIsRunning = true;
        }
        else{
            Logger.d("InstanceKeeper running");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("InstanceKeeperService destroyed");
        //arielPubNub.cleanUp();
        mIsRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
