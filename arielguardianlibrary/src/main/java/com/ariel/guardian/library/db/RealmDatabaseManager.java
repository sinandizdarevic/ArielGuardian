package com.ariel.guardian.library.db;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.db.realm.model.DeviceApplication;
import com.ariel.guardian.library.db.realm.model.WrapperMessage;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import com.ariel.guardian.library.db.realm.ArielLibraryModule;

/**
 * Created by mikalackis on 14.10.16..
 */

public class RealmDatabaseManager implements ArielDatabaseInterface {

    public static final String TAG = "RealmDatabaseManager";

    private static RealmDatabaseManager mInstance;

    private RealmConfiguration mRealmConfiguration;

    public static RealmDatabaseManager getInstance(final Context context){
        if(mInstance==null){
            mInstance = new RealmDatabaseManager(context);
        }
        return mInstance;
    }

    private RealmDatabaseManager(final Context context){
        Realm.init(context);
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        final File externalFilesDir = context.getExternalFilesDir(null);
        Log.i("ABRAKADABRA", "File path: "+externalFilesDir.getAbsolutePath());
        mRealmConfiguration = new RealmConfiguration.Builder()
                //.encryptionKey(key)
                .directory(externalFilesDir)
                .name("ariel.realm")
                .modules(new ArielLibraryModule())
                .build();
    }

    @Override
    public long createOrUpdateApplication(DeviceApplication object) {
        // add new application to realm
        Realm realm = openRealmTransaction();
        DeviceApplication newApp = realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        Log.i(TAG, "Saved application with id: "+newApp.getId());
        return newApp.getId();
    }

    @Override
    public long saveWrapperMessage(WrapperMessage message) {
        // add new application to realm
        Realm realm = openRealmTransaction();
        WrapperMessage wrapperMessage = realm.copyToRealmOrUpdate(message);
        realm.commitTransaction();
        Log.i(TAG, "Saved application with id: "+wrapperMessage.getId());
        return wrapperMessage.getId();
    }

    @Override
    public void removeWrapperMessage(long id) {
        Realm realm = openRealmTransaction();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        RealmResults<WrapperMessage> result1 = query.findAll();
        WrapperMessage wrapperMessage = result1.first();
        wrapperMessage.deleteFromRealm();
        commitAndCloseTransaction(realm);
    }

    @Override
    public List<DeviceApplication> getDeviceApplications(boolean managed) {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        RealmResults<DeviceApplication> result1 = query.findAll();
        if(!managed){
            return realm.copyFromRealm(result1);
        }
        else{
            return result1;
        }
    }

    @Override
    public WrapperMessage getUnmanagedWrapperMessageById(long id) {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        RealmResults<WrapperMessage> result1 = query.findAll();
        WrapperMessage wrapperMessage = realm.copyFromRealm(result1.first());
        realm.close();
        return wrapperMessage;
    }

    @Override
    public DeviceApplication getDeviceApplicationById(long id, final boolean managed) {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        query.equalTo("id", id);
        RealmResults<DeviceApplication> result1 = query.findAll();
        DeviceApplication deviceApp = null;
        if(!managed){
            deviceApp = realm.copyFromRealm(result1.first());
        }
        else{
            deviceApp = result1.first();
        }
        return deviceApp;
    }

    private Realm openRealmTransaction(){
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        return realm;
    }

    private void commitAndCloseTransaction(final Realm realm){
        realm.commitTransaction();
        realm.close();
    }
}
