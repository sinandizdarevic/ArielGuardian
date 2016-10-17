package com.ariel.guardian.library;

import android.content.Context;

import com.ariel.guardian.library.db.ArielDatabaseInterface;
import com.ariel.guardian.library.db.RealmDatabaseManager;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.pubnub.ArielPubNubInterface;

/**
 * Created by mikalackis on 6.10.16..
 */

public final class Ariel implements ArielInterface {

    public static final String TAG = "Ariel";

    private static Ariel mInstance;

    private static String mPubNubChannel;

    private static Context mApplicationContext;

    private static ArielPubNub mPubNub;

    private Ariel(final Context context, final String pubNubChannel){
        mApplicationContext = context;
        mPubNubChannel = pubNubChannel;
        // initialize realm database here
        initRealm();
        initReceivers();
        initPubNub();
    }

    private void initReceivers(){
    }

    private void initRealm(){
        RealmDatabaseManager.getInstance(mApplicationContext);
    }

    private void initPubNub(){
        mPubNub = new ArielPubNub(mApplicationContext,mPubNubChannel);
    }

    public static synchronized void init(final Context context, final String pubNubChannel){
        if(mInstance==null) {
            mInstance = new Ariel(context, pubNubChannel);
        }
    }

    public static ArielInterface action(){
        return mInstance;
    }

    @Override
    public ArielDatabaseInterface database() {
        return RealmDatabaseManager.getInstance(mApplicationContext);
    }

    @Override
    public ArielPubNubInterface pubnub(){
        return mPubNub;
    }

    public static Context getMyApplicationContext(){
        return mApplicationContext;
    }

}
