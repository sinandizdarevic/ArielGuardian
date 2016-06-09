
package com.ariel.guardian.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DeviceConfiguration {

    private long leaseStartDate;
    private long leaseEndDate;
    private boolean masterDevice;
    private int arielSystemStatus;
    private String phoneNumber;
    private String clientId;
    private boolean active;
    private long locationTrackingInterval;

    public DeviceConfiguration() {
        // default constructor required by firebase
    }

    public long getLeaseStartDate() {
        return leaseStartDate;
    }

    public long getLeaseEndDate() {
        return leaseEndDate;
    }

    public boolean isMasterDevice() {
        return masterDevice;
    }

    public int getArielSystemStatus() {
        return arielSystemStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isActive() {
        return active;
    }

    public long getLocationTrackingInterval(){
        return locationTrackingInterval;
    }

}
