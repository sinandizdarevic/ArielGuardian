package com.ariel.guardian.library.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * POJO class that represents device application object in firebase database
 */
@IgnoreExtraProperties
public class DeviceApplication {

    /**
     * Application package name
     */
    public String packageName;
    /**
     * Human readable application name
     */
    public String appName;
    /**
     * Last modification date (DO WE NEED THIS??)
     */
    public long date;
    /**
     * Denotes if application is installed on the device or not
     */
    public boolean installed;
    /**
     * Application status field
     */
    public boolean disabled;

    public DeviceApplication(){

    }

    public DeviceApplication(String packageName, String appName, long date, boolean installed) {
        this.appName = appName;
        this.date = date;
        this.installed = installed;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
