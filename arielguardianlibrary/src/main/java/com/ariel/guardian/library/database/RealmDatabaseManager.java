package com.ariel.guardian.library.database;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mikalackis on 14.10.16..
 */

public final class RealmDatabaseManager {

    public static final String TAG = "RealmDatabaseManager";

    private static volatile RealmDatabaseManager mInstance = null;

    private RealmConfiguration mRealmConfiguration;

    public static RealmDatabaseManager getInstance(final Context context) {
        if (mInstance == null) {
            synchronized (RealmDatabaseManager.class){
                if(mInstance == null){
                    mInstance = new RealmDatabaseManager(context);
                }
            }
            mInstance = new RealmDatabaseManager(context);
        }
        return mInstance;
    }

    private RealmDatabaseManager(final Context context) {
        Realm.init(context);
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        final File externalFilesDir = context.getExternalFilesDir(null);
        Logger.d("File path: " + externalFilesDir.getAbsolutePath());
        Logger.d( "Context files dir path: " + context.getFilesDir().getAbsolutePath());
        mRealmConfiguration = new RealmConfiguration.Builder()
                //.encryptionKey(key)
                .name("ariel.realm")
                .build();
    }

    public Realm getRealmInstance(){
        return Realm.getInstance(mRealmConfiguration);
    }

    public RealmConfiguration getRealmConfiguration(){
        return mRealmConfiguration;
    }

    public void cleanUp(){
        mRealmConfiguration = null;
    }

}
