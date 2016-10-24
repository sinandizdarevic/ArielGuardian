package com.ariel.guardian.library.db;

import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;

import java.util.List;

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

}
