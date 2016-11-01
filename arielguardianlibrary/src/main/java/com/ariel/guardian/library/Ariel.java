package com.ariel.guardian.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.library.db.ArielDatabase;
import com.ariel.guardian.library.db.ArielDatabaseInterface;
import com.ariel.guardian.library.db.RealmDatabaseManager;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.pubnub.ArielPubNubInterface;
import com.ariel.guardian.library.services.InstanceKeeperService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by mikalackis on 6.10.16..
 */

public final class Ariel implements ArielInterface {

    public static final String TAG = "Ariel";

    private static Ariel mInstance;

    private static Context mApplicationContext;

    private static ArielPubNub mPubNub;

    private static ArielDatabase mDatabase;

    private Ariel(final Context context) {
        mApplicationContext = context;
        // initialize realm database here
        initDb();
        initPubNub();
        initInstanceKeeper();
    }

    private void initInstanceKeeper() {
        Intent instanceKeeperService = new Intent(mApplicationContext, InstanceKeeperService.class);
        mApplicationContext.startService(instanceKeeperService);
    }

    private void initDb() {
        mDatabase = new ArielDatabase(mApplicationContext);
    }

    private void initPubNub() {
        mPubNub = new ArielPubNub(mApplicationContext, mDatabase);
    }

    public static synchronized void init(final Context context) {
        if (mInstance == null) {
            mInstance = new Ariel(context);
        }
    }

    public static ArielInterface action() {
        return mInstance;
    }

    @Override
    public ArielDatabaseInterface database() {
        return mDatabase;
    }

    @Override
    public ArielPubNubInterface pubnub() {
        return mPubNub;
    }

    public static Context getMyApplicationContext() {
        return mApplicationContext;
    }

}
