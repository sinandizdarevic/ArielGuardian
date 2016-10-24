package com.ariel.guardian.library.db;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by mikalackis on 14.10.16..
 */

public class RealmDatabaseManager implements ArielDatabaseInterface {

    public static final String TAG = "RealmDatabaseManager";

    private static RealmDatabaseManager mInstance;

    private RealmConfiguration mRealmConfiguration;

    public static RealmDatabaseManager getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new RealmDatabaseManager(context);
        }
        return mInstance;
    }

    private RealmDatabaseManager(final Context context) {
        Realm.init(context);
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        final File externalFilesDir = context.getExternalFilesDir(null);
        Log.i("ABRAKADABRA", "File path: "+externalFilesDir.getAbsolutePath());
        Log.i("ABRAKADABRA", "Context files dir path: "+context.getFilesDir().getAbsolutePath());
        mRealmConfiguration = new RealmConfiguration.Builder()
                //.encryptionKey(key)
                .name("ariel.realm")
                .modules(new ArielLibraryModule())
                .build();
    }

    @Override
    public void createOrUpdateApplication(DeviceApplication deviceApplication) {
        Log.i(TAG, "Creating application: " + deviceApplication.getPackageName());
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        DeviceApplication newApp = realm.copyToRealmOrUpdate(deviceApplication);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteApplication(DeviceApplication deviceApplication) {
        Log.i(TAG, "TODO: Removing application: " + deviceApplication.getPackageName());
    }

    @Override
    public DeviceApplication getApplicationByID(String packageName) {
        Log.i(TAG, "Get application by id: " + packageName);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        query.equalTo("packageName", packageName);
        RealmResults<DeviceApplication> result1 = query.findAll();
        DeviceApplication deviceApp = realm.copyFromRealm(result1.first());
        return deviceApp;
    }

    @Override
    public List<DeviceApplication> getAllApplications() {
        Log.i(TAG, "Get all applications");
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        RealmResults<DeviceApplication> result1 = query.findAll();
        return realm.copyFromRealm(result1);
    }

    @Override
    public void createConfiguration(Configuration deviceConfiguration) {
        Log.i(TAG, "Creating device configuration with id: " + deviceConfiguration.getId());
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        Configuration newApp = realm.copyToRealmOrUpdate(deviceConfiguration);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteConfiguration(long id) {
        Log.i(TAG, "TODO: deleteConfiguration()");
    }

    @Override
    public Configuration getConfigurationByID(long id) {
        Log.i(TAG, "Get configuration by id: " + id);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<Configuration> query = realm.where(Configuration.class);
        query.equalTo("id", id);
        RealmResults<Configuration> result1 = query.findAll();
        Configuration deviceConfiguration = realm.copyFromRealm(result1.first());
        return deviceConfiguration;
    }

    @Override
    public Configuration getActiveConfiguration() {
        Log.i(TAG, "Get active configuration");
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<Configuration> query = realm.where(Configuration.class);
        query.equalTo("active", true);
        RealmResults<Configuration> result1 = query.findAll();
        Configuration deviceConfiguration = realm.copyFromRealm(result1.first());
        return deviceConfiguration;
    }

    @Override
    public void createLocation(DeviceLocation deviceLocation) {
        Log.i(TAG, "Create location with id: " + deviceLocation);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        DeviceLocation newApp = realm.copyToRealmOrUpdate(deviceLocation);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteLocation(long id) {
        Log.i(TAG, "TODO: deleteLocation()");
    }

    @Override
    public DeviceLocation getLocationByID(long id) {
        Log.i(TAG, "Get location by id: "+id);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<DeviceLocation> query = realm.where(DeviceLocation.class);
        query.equalTo("id", id);
        RealmResults<DeviceLocation> result1 = query.findAll();
        DeviceLocation deviceLocation = realm.copyFromRealm(result1.first());
        return deviceLocation;
    }

    @Override
    public DeviceLocation getLastLocation() {
        Log.i(TAG, "TODO: getLastLocation()");
        return new DeviceLocation();
    }

    @Override
    public void createWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG, "Create wrapper message with id: " + wrapperMessage.getId());
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        WrapperMessage newApp = realm.copyToRealmOrUpdate(wrapperMessage);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteWrapperMessageByID(long id) {
        Log.i(TAG, "Delete wrapper message with id: " + id);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        RealmResults<WrapperMessage> result1 = query.findAll();
        WrapperMessage wrapperMessage = result1.first();
        wrapperMessage.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG, "Detelet wrapper message with id: " + wrapperMessage.getId());
        deleteWrapperMessageByID(wrapperMessage.getId());
    }

    @Override
    public WrapperMessage getWrapperMessageByID(long id) {
        Log.i(TAG, "Get wrapper message by id: "+id);
        Realm realm = Realm.getInstance(mRealmConfiguration);
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        RealmResults<WrapperMessage> result1 = query.findAll();
        WrapperMessage wrapperMessage = realm.copyFromRealm(result1.first());
        return wrapperMessage;
    }

}
