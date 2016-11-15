package com.ariel.guardian.library.database;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.orhanobut.logger.Logger;

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

    public ArielDatabase(final Context context) {
        this.mContext = context;
        realmDatabaseManager = RealmDatabaseManager.getInstance(context);
    }

    @Override
    public void createOrUpdateApplication(DeviceApplication deviceApplication) {
        Logger.d( "Creating application: " + deviceApplication.getPackageName());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        DeviceApplication newApp = realm.copyToRealmOrUpdate(deviceApplication);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteApplication(DeviceApplication deviceApplication) {
        Logger.d( "Removing application: " + deviceApplication.getPackageName());
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
        Logger.d( "Get application by id: " + packageName);
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
        Logger.d( "Get all applications");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<DeviceApplication> query = realm.where(DeviceApplication.class);
        RealmResults<DeviceApplication> result1 = query.findAll();
        List<DeviceApplication> apps = realm.copyFromRealm(result1);
        realm.close();
        return apps;
    }

    @Override
    public void createConfiguration(Configuration deviceConfiguration) {
        Logger.d( "Creating device configuration with id: " + deviceConfiguration.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        Configuration newApp = realm.copyToRealmOrUpdate(deviceConfiguration);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteConfiguration(long id) {
        Logger.d( "TODO: deleteConfiguration()");
    }

    @Override
    public Configuration getConfigurationByID(long id) {
        Logger.d( "Get configuration by id: " + id);
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
        Logger.d( "Get active configuration");
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
        Logger.d( "Create location with id: " + deviceLocation);
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        DeviceLocation newApp = realm.copyToRealmOrUpdate(deviceLocation);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteLocation(long id) {
        Logger.d( "TODO: deleteLocation()");
    }

    @Override
    public DeviceLocation getLocationByID(long id) {
        Logger.d( "Get location by id: " + id);
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
        Logger.d( "TODO: getLastLocation()");
        return new DeviceLocation();
    }

    @Override
    public void createWrapperMessage(WrapperMessage wrapperMessage) {
        Logger.d( "Create wrapper message with id: " + wrapperMessage.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        Logger.d( "Number of messages before creation: " + realm.where(WrapperMessage.class).count());
        realm.where(WrapperMessage.class).count();
        WrapperMessage newApp = realm.copyToRealmOrUpdate(wrapperMessage);
        realm.commitTransaction();
        Logger.d( "Number of messages after creation: " + realm.where(WrapperMessage.class).count());
        realm.close();
    }

    @Override
    public void deleteWrapperMessageByID(long id) {
        Logger.d( "Delete wrapper message with id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        Logger.d( "Number of messages before delete: " + realm.where(WrapperMessage.class).count());
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        WrapperMessage wrapperMessage = query.findFirst();
        if (wrapperMessage != null) {
            realm.beginTransaction();
            Logger.d( "Found and deleting wrapper messaage: " + id);
            wrapperMessage.deleteFromRealm();
            realm.commitTransaction();
        }
        Logger.d( "Number of messages after delete: " + realm.where(WrapperMessage.class).count());
        realm.close();
    }

    @Override
    public void deleteWrapperMessage(WrapperMessage wrapperMessage) {
        Logger.d( "Detelet wrapper message with id: " + wrapperMessage.getId());
        deleteWrapperMessageByID(wrapperMessage.getId());
    }

    @Override
    public WrapperMessage getWrapperMessageByID(long id) {
        Logger.d( "Get wrapper message by id: " + id);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("id", id);
        WrapperMessage message = query.findFirst();
        if (message != null) {
            WrapperMessage wrapperMessage = realm.copyFromRealm(query.findFirst());
            realm.close();
            return wrapperMessage;
        } else {
            realm.close();
            return null;
        }
    }

    @Override
    public List<WrapperMessage> getUnsentWrapperMessages() {
        Logger.d( "Get all WrapperMessages");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("sent", false);
        RealmResults<WrapperMessage> result1 = query.findAll();
        List<WrapperMessage> wrapperMessages = null;
        if (result1 != null) {
            wrapperMessages = realm.copyFromRealm(result1);
        }
        realm.close();
        return wrapperMessages;
    }

    @Override
    public RealmResults<WrapperMessage> getWaitingForExecutionMessages(String messageType) {
        Logger.d( "getWaitingForExecutionMessages");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<WrapperMessage> query = realm.where(WrapperMessage.class);
        query.equalTo("sent", true).equalTo("messageType", messageType).equalTo("reportReception", true);
        RealmResults<WrapperMessage> result1 = query.findAll();
        if (result1 != null) {
            Logger.d( "Result size: " + result1.size());
        } else {
            Logger.d( "Result is null");
        }
        realm.close();
        return result1;
    }

    @Override
    public void createDevice(ArielDevice device) {
        Logger.d( "Create device message with id: " + device.getId());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        ArielDevice newDevice = realm.copyToRealmOrUpdate(device);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<ArielDevice> getAllDevices() {
        Logger.d( "Get all devices");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielDevice> query = realm.where(ArielDevice.class);
        RealmResults<ArielDevice> result1 = query.findAll();
        List<ArielDevice> devices = realm.copyFromRealm(result1);
        realm.close();
        return devices;
    }

    @Override
    public ArielDevice getDeviceByID(String deviceID) {
        Logger.d( "Get device by id: " + deviceID);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielDevice> query = realm.where(ArielDevice.class);
        query.equalTo("deviceUID", deviceID);
        ArielDevice arielDevice = query.findFirst();
        if (arielDevice != null) {
            Logger.d( "Found device: " + arielDevice.getArielChannel());
        } else {
            Logger.d( "ArielDevice not found!!!");
        }
        realm.close();
        return arielDevice;
    }

    @Override
    public RealmConfiguration getRealmConfiguration() {
        return realmDatabaseManager.getRealmConfiguration();
    }

    @Override
    public void createOrUpdateMaster(ArielMaster master) {
        Logger.d( "Create device message with id: " + master.getDeviceUID());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        ArielMaster newMaster = realm.copyToRealmOrUpdate(master);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteMaster(ArielMaster master) {
        Logger.d( "Delete master with id: " + master.getDeviceUID());
        Realm realm = realmDatabaseManager.getRealmInstance();
        realm.beginTransaction();
        master.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<ArielMaster> getAllMasters() {
        Logger.d( "Get all masters");
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielMaster> query = realm.where(ArielMaster.class);
        RealmResults<ArielMaster> result1 = query.findAll();
        List<ArielMaster> masters = realm.copyFromRealm(result1);
        realm.close();
        return masters;
    }

    @Override
    public ArielMaster getMasterByID(String uid) {
        Logger.d( "Get master by id: " + uid);
        Realm realm = realmDatabaseManager.getRealmInstance();
        RealmQuery<ArielMaster> query = realm.where(ArielMaster.class);
        query.equalTo("deviceUID", uid);
        ArielMaster arielMaster = query.findFirst();
        if (arielMaster != null) {
            Logger.d( "Found master: " + arielMaster.getDeviceUID());
        } else {
            Logger.d( "ArielMaster not found!!!");
        }
        realm.close();
        return arielMaster;
    }
}
