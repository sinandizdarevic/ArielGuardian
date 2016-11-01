package com.ariel.guardian.library.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.library.db.RealmDatabaseManager;
import com.ariel.guardian.library.pubnub.PubNubManager;

/**
 * Class responsible for managing PubNubManager instance.
 */
public class InstanceKeeperService extends Service{

    private static final String TAG = "InstanceKeeperService";

    private PubNubManager mPubNubManager;
    private RealmDatabaseManager mRealmDatabaseManager;

    private boolean mIsRunning;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Initating Instances");

        if(!mIsRunning) {
            Log.i(TAG,"InstanceKeeper not running");
            mPubNubManager = PubNubManager.getInstance(getApplicationContext());
            mRealmDatabaseManager = RealmDatabaseManager.getInstance(getApplicationContext());
            mIsRunning = true;
        }
        else{
            Log.i(TAG,"InstanceKeeper running");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"InstanceKeeperService destroyed");
        mPubNubManager.cleanUp();
        mRealmDatabaseManager.cleanUp();
        mIsRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
