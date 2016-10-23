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
public class Configuration extends ArielModel {

    @Column
    @PrimaryKey
    long id;

    /**
     * Ariel systems status field
     */
    @Column
    private int arielSystemStatus;
    /**
     * Time interval in which the device will automatically upload its location
     */
    @Column
    private long locationTrackingInterval;

    @Column
    private boolean constantTracking;

    @Column
    private boolean active;

    public int getArielSystemStatus() {
        return arielSystemStatus;
    }

    public void setArielSystemStatus(int arielSystemStatus) {
        this.arielSystemStatus = arielSystemStatus;
    }

    public long getLocationTrackingInterval() {
        return locationTrackingInterval;
    }

    public void setLocationTrackingInterval(long locationTrackingInterval) {
        this.locationTrackingInterval = locationTrackingInterval;
    }

    public boolean isConstantTracking() {
        return constantTracking;
    }

    public void setConstantTracking(boolean constantTracking) {
        this.constantTracking = constantTracking;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
