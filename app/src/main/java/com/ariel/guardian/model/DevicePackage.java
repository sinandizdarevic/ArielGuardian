package com.ariel.guardian.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DevicePackage {

    public String packageName;
    public String appName;
    public long date;
    public boolean installed;
    public boolean disabled;

    public DevicePackage(){

    }

    public DevicePackage(String packageName, String appName, long date, boolean installed) {
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
