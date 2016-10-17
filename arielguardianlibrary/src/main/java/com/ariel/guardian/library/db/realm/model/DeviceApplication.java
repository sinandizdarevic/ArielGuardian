package com.ariel.guardian.library.db.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 5.10.16..
 */

public class DeviceApplication extends RealmObject {

    @PrimaryKey
    private long id;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
