package com.ariel.guardian.library.db.model;

import com.ariel.guardian.library.db.ArielDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by mikalackis on 5.10.16..
 */

@Table(database = ArielDatabase.class)
public class DeviceApplication extends ArielModel {

    /**
     * DeviceApplication package name
     */
    @Column
    @PrimaryKey
    private String packageName;
    /**
     * Human readable application name
     */
    @Column
    private String appName;
    /**
     * DeviceApplication status field
     */
    @Column
    private boolean disabled;

    @Column
    private long disabledUntil;

    /**
     * Human readable application name
     */
    @Column
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
}
