package com.ariel.guardian.library.database;

import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.database.model.WrapperMessage;

import java.util.List;

import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielDatabaseInterface {

    // Application methods
    void createOrUpdateApplication(final DeviceApplication deviceApplication);
    void deleteApplication(final DeviceApplication deviceApplication);
    DeviceApplication getApplicationByID(final String packageName);
    List<DeviceApplication> getAllApplications();

    // Configuration methods
    void createConfiguration(final Configuration deviceConfiguration);
    void deleteConfiguration(final long id);
    Configuration getConfigurationByID(final long id);
    Configuration getActiveConfiguration();

    // Location methods
    void createLocation(final DeviceLocation deviceLocation);
    void deleteLocation(final long id);
    DeviceLocation getLocationByID(final long id);
    DeviceLocation getLastLocation();

    // Wraper methods
    void createWrapperMessage(final WrapperMessage wrapperMessage);
    void deleteWrapperMessageByID(final long id);
    void deleteWrapperMessage(final WrapperMessage wrapperMessage);
    WrapperMessage getWrapperMessageByID(final long id);
    List<WrapperMessage> getUnsentWrapperMessages();
    RealmResults<WrapperMessage> getWaitingForExecutionMessages(final String type);

    // ArielDevice methods
    void createDevice(final ArielDevice device);
    List<ArielDevice> getAllDevices();
    ArielDevice getDeviceByID(final String deviceID);

    // Realm methods
    RealmConfiguration getRealmConfiguration();

    // Device master methods
    void createOrUpdateMaster(final ArielMaster master);
    void deleteMaster(final ArielMaster master);
    List<ArielMaster> getAllMasters();
    ArielMaster getMasterByID(final String uid);

}
