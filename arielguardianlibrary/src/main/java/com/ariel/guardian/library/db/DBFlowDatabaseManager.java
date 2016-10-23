package com.ariel.guardian.library.db;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.Configuration_Table;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceApplication_Table;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.DeviceLocation_Table;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.ariel.guardian.library.db.model.WrapperMessage_Table;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by mikalackis on 14.10.16..
 */

public class DBFlowDatabaseManager implements ArielDatabaseInterface {

    public static final String TAG = "DBFlowDatabaseManager";

    private static DBFlowDatabaseManager mInstance;

    public static DBFlowDatabaseManager getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new DBFlowDatabaseManager(context);
        }
        return mInstance;
    }

    private DBFlowDatabaseManager(final Context context) {
        Log.i(TAG, "Initializing FlowManager");
        FlowManager.init(new FlowConfig.Builder(context).build());
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
    }

    @Override
    public void createOrUpdateApplication(DeviceApplication deviceApplication) {
        Log.i(TAG, "Creating application: " + deviceApplication.getPackageName());
        deviceApplication.save();
    }

    @Override
    public void deleteApplication(DeviceApplication deviceApplication) {
        Log.i(TAG, "Removing application: " + deviceApplication.getPackageName());
        deviceApplication.delete();
    }

    @Override
    public DeviceApplication getApplicationByID(String packageName) {
        Log.i(TAG, "Get application by id: " + packageName);
        DeviceApplication deviceApp = SQLite.select().
                from(DeviceApplication.class).
                where(DeviceApplication_Table.packageName.eq(packageName)).querySingle();
        return deviceApp;
    }

    @Override
    public DeviceApplication getApplicationByPackageName(String packageName) {
        Log.i(TAG, "Get application by packageName: " + packageName);
        DeviceApplication deviceApp = SQLite.select().
                from(DeviceApplication.class).
                where(DeviceApplication_Table.packageName.eq(packageName)).querySingle();
        return deviceApp;
    }

    @Override
    public List<DeviceApplication> getAllApplications() {
        Log.i(TAG, "Get all applications");
        List<DeviceApplication> deviceApps = SQLite.select().
                from(DeviceApplication.class).queryList();
        return deviceApps;
    }

    @Override
    public void createConfiguration(Configuration deviceConfiguration) {
        Log.i(TAG, "Creating device configuration with id: " + deviceConfiguration.getId());
        deviceConfiguration.save();
    }

    @Override
    public void deleteConfiguration(long id) {
        Log.i(TAG, "TODO: deleteConfiguration()");
    }

    @Override
    public Configuration getConfigurationByID(long id) {
        Configuration deviceConfiguration = SQLite.select().
                from(Configuration.class).
                where(Configuration_Table.id.eq(id)).querySingle();
        return deviceConfiguration;
    }

    @Override
    public Configuration getActiveConfiguration() {
        Log.i(TAG, "Get active configuration");
        Configuration deviceConfiguration = SQLite.select().
                from(Configuration.class).
                where(Configuration_Table.active.eq(true)).querySingle();
        return deviceConfiguration;
    }

    @Override
    public void createLocation(DeviceLocation deviceLocation) {
        Log.i(TAG, "Create location with id: " + deviceLocation);
        deviceLocation.save();
    }

    @Override
    public void deleteLocation(long id) {
        Log.i(TAG, "TODO: deleteLocation()");
    }

    @Override
    public DeviceLocation getLocationByID(long id) {
        DeviceLocation deviceLocation = SQLite.select().
                from(DeviceLocation.class).
                where(DeviceLocation_Table.id.eq(id)).querySingle();
        return deviceLocation;
    }

    @Override
    public DeviceLocation getLastLocation() {
        DeviceLocation deviceLocation = SQLite.select().
                from(DeviceLocation.class)
                .where().orderBy(DeviceLocation_Table.timestamp, true).querySingle();
        return deviceLocation;
    }

    @Override
    public void createWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG, "Create wrapper message with id: " + wrapperMessage.getId());
        wrapperMessage.save();
    }

    @Override
    public void deleteWrapperMessageByID(long id) {
        Log.i(TAG, "Detelet wrapper message with id: " + id);
        WrapperMessage wrapperMessage = getWrapperMessageByID(id);
        if (wrapperMessage != null) {
            Log.i(TAG, "Removing wrapper message with id: " + wrapperMessage.getId());
            wrapperMessage.delete();
        }
    }

    @Override
    public void deleteWrapperMessage(WrapperMessage wrapperMessage) {
        Log.i(TAG, "Detelet wrapper message with id: " + wrapperMessage.getId());
        wrapperMessage.delete();
    }

    @Override
    public WrapperMessage getWrapperMessageByID(long id) {
        WrapperMessage wrapperMessage = SQLite.select().
                from(WrapperMessage.class).
                where(WrapperMessage_Table.id.eq(id)).querySingle();
        return wrapperMessage;
    }

}
