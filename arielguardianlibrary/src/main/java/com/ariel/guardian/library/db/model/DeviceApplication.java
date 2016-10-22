package com.ariel.guardian.library.db.model;

import android.bluetooth.BluetoothClass;

import com.orm.SugarRecord;

/**
 * Created by mikalackis on 5.10.16..
 */

public class DeviceApplication extends SugarRecord {

    /**
     * DeviceApplication package name
     */
    private String packageName;
    /**
     * Human readable application name
     */
    private String appName;
    /**
     * DeviceApplication status field
     */
    private boolean disabled;

    private long disabledUntil;

    public DeviceApplication(){}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
