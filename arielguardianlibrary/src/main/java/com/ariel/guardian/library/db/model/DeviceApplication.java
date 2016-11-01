package com.ariel.guardian.library.db.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 5.10.16..
 */

public class DeviceApplication extends RealmObject {

    /**
     * DeviceApplication package name
     */
    @PrimaryKey
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

    /**
     * Used as a helper field to display progress when updating
     */
    @Ignore
    private transient boolean pending;

    /**
     * Human readable application name
     */
    private boolean uninstalled;

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

    public long getDisabledUntil() {
        return disabledUntil;
    }

    public void setDisabledUntil(long disabledUntil) {
        this.disabledUntil = disabledUntil;
    }

    public boolean isUninstalled() {
        return uninstalled;
    }

    public void setUninstalled(boolean uninstalled) {
        this.uninstalled = uninstalled;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
