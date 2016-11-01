package com.ariel.guardian.library.db;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.db.model.ArielDevice;
import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by mikalackis on 29.10.16..
 */

public final class ArielDatabase implements ArielDatabaseInterface {

    public static final String TAG = "ArielDatabase";

    private Context mContext;

    private RealmDatabaseManager realmDatabaseManager;

    public ArielDatabase(final Context context){
        this.mContext = context;
        realmDatabaseManager = RealmDatabaseManager.getInstance(context);
    }

    @Override
    public void createOrUpdateApplication(DeviceApplication deviceApplication) {
        Log.i(TAG,"Creating application: " + deviceApplication.getPackageName());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        DeviceApplication newApp = realm.copyToRealmOrUpdate(deviceApplication);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteApplication(DeviceApplication deviceApplication) {
        Log.i(TAG,"Removing application: " + deviceApplication.getPackageName());
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        query.equalTo("packageName", deviceApplication.getPackageName());
        RealmResults<DeviceApplication> result1 = query.findAll();
        if (result1 != null && result1.size() == 1) {
            DeviceApplication da = result1.first();
            realm.beginTransaction();
            da.deleteFromRealm();
            realm.commitTransaction();
        }
        realm.close();
    }

    @Override
    public DeviceApplication getApplicationByID(String packageName) {
        Log.i(TAG,"Get application by id: " + packageName);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        query.equalTo("packageName", packageName);
        RealmResults<DeviceApplication> result1 = query.findAll();
        if (result1 != null && result1.size() == 1) {
            DeviceApplication deviceApp = realm.copyFromRealm(result1.first());
            realm.close();
            return deviceApp;
        } else {
            realm.close();
            return null;
        }
    }

    @Override
    public List<DeviceApplication> getAllApplications() {
        Log.i(TAG,"Get all applications");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        RealmResults<DeviceApplication> result1 = query.findAll();
        List<DeviceApplication> apps = realm.copyFromRealm(result1);
        realm.close();
        return apps;
    }

    @Override
    public void createConfiguration(Configuration deviceConfiguration) {
        Log.i(TAG,"Creating device configuration with id: " + deviceConfiguration.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        Configuration newApp = realm.copyToRealmOrUpdate(deviceConfiguration);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteConfiguration(long id) {
        Log.i(TAG,"TODO: deleteConfiguration()");
    }

    @Override
    public Configuration getConfigurationByID(long id) {
        Log.i(TAG,"Get configuration by id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<Configuration> query = realm.where(Configuration.class);
        query.equalTo("id", id);
        RealmResults<Configuration> result1 = query.findAll();
        Configuration deviceConfiguration = realm.copyFromRealm(result1.first());
        realm.close();
        return deviceConfiguration;
    }

    @Override
    public Configuration getActiveConfiguration() {
        Log.i(TAG,"Get active configuration");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<Configuration> query = realm.where(Configuration.class);
        query.equalTo("active", true);
        RealmResults<Configuration> result1 = query.findAll();
        Configuration deviceConfiguration = realm.copyFromRealm(result1.first());
        realm.close();
        return deviceConfiguration;
    }

    @Override
    public void createLocation(DeviceLocation deviceLocation) {
        Log.i(TAG,"Create location with id: " + deviceLocation);
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        DeviceLocation newApp = realm.copyToRealmOrUpdate(deviceLocation);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteLocation(long id) {
        Log.i(TAG,"TODO: deleteLocation()");
    }

    @Override
    public DeviceLocation getLocationByID(long id) {
        Log.i(TAG,"Get location by id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<DeviceLocation> query = realm.where(DeviceLocation.class);
        query.equalTo("id", id);
        RealmResults<DeviceLocation> result1 = query.findAll();
        DeviceLocation deviceLocation = realm.copyFromRealm(result1.first());
        realm.close();
        return deviceLocation;
    }

    @Override
    public DeviceLocation getLastLocation() {
        Log.i(TAG,"TODO: getLastLocation()");
        return new DeviceLocation();
    }

    @Override
    public void createWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG,"Create wrapper message with id: " + wrapperMessage.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        WrapperMessage newApp = realm.copyToRealmOrUpdate(wrapperMessage);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteWrapperMessageByID(long id) {
        Log.i(TAG,"Delete wrapper message with id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        WrapperMessage wrapperMessage = query.findFirst();
        if(wrapperMessage!=null) {
            realm.beginTransaction();
            Log.i(TAG,"Found and deleting wrapper messaage: " + id);
            wrapperMessage.deleteFromRealm();
            realm.commitTransaction();
        }
        realm.close();
    }

    @Override
    public void deleteWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG,"Detelet wrapper message with id: " + wrapperMessage.getId());
        deleteWrapperMessageByID(wrapperMessage.getId());
    }

    @Override
    public WrapperMessage getWrapperMessageByID(long id) {
        Log.i(TAG,"Get wrapper message by id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        WrapperMessage message = query.findFirst();
        if(message!=null) {
            WrapperMessage wrapperMessage = realm.copyFromRealm(query.findFirst());
            realm.close();
            return wrapperMessage;
        }
        else{
            realm.close();
            return null;
        }
    }

    @Override
    public List<WrapperMessage> getUnsentWrapperMessages() {
        Log.i(TAG,"Get all WrapperMessages");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("sent", false);
        RealmResults<WrapperMessage> result1 = query.findAll();
        List<WrapperMessage> wrapperMessages = null;
        if(result1!=null) {
            wrapperMessages = realm.copyFromRealm(result1);
        }
        realm.close();
        return wrapperMessages;
    }

    @Override
    public RealmResults<WrapperMessage> getWaitingForExecutionMessages(String messageType) {
        Log.i(TAG,"getWaitingForExecutionMessages");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("sent", true).equalTo("messageType", messageType).equalTo("reportReception", true);
        RealmResults<WrapperMessage> result1 = query.findAll();
        if(result1!=null){
            Log.i(TAG, "Result size: "+result1.size());
        }
        else{
            Log.i(TAG, "Result is null");
        }
        realm.close();
        return result1;
    }

    @Override
    public void createDevice(ArielDevice device) {
        Log.i(TAG,"Create device message with id: " + device.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        ArielDevice newDevice = realm.copyToRealmOrUpdate(device);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<ArielDevice> getAllDevices() {
        Log.i(TAG,"Get all devices");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielDevice> query = realm.where(ArielDevice.class);
        RealmResults<ArielDevice> result1 = query.findAll();
        List<ArielDevice> devices = realm.copyFromRealm(result1);
        realm.close();
        return devices;
    }

    @Override
    public ArielDevice getDeviceByID(String deviceID) {
        Log.i(TAG,"Get device by id: "+deviceID);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielDevice> query = realm.where(ArielDevice.class);
        query.equalTo("deviceUID", deviceID);
        ArielDevice arielDevice = query.findFirst();
        if(arielDevice!=null){
            Log.i(TAG,"Found device: "+arielDevice.getArielChannel());
        }
        else{
            Log.i(TAG,"ArielDevice not found!!!");
        }
        realm.close();
        return arielDevice;
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return realmDatabaseManager.getRealmConfiguration();
    }
}
