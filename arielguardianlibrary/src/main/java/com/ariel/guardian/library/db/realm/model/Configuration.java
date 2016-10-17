package com.ariel.guardian.library.db.realm.model;

import io.realm.RealmObject;

/**
 * Created by mikalackis on 5.10.16..
 */

public class Configuration extends RealmObject {

    /**
     * Ariel systems status field
     */
    private int arielSystemStatus;
    /**
     * Time interval in which the device will automatically upload its location
     */
    private long locationTrackingInterval;

    private boolean constantTracking;

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
}
