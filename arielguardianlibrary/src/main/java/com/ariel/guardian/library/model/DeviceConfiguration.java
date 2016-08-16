
package com.ariel.guardian.library.model;

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
    private String pubNubCipherKey;
    private String pubNubSubscribeKey;
    private String pubNubPublishKey;
    private String pubNubSecretKey;
    private boolean pubNubUseSSL;

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

    public String getPubNubCipherKey() {
        return pubNubCipherKey;
    }

    public String getPubNubSubscribeKey() {
        return pubNubSubscribeKey;
    }

    public String getPubNubPublishKey() {
        return pubNubPublishKey;
    }

    public String getPubNubSecretKey() {
        return pubNubSecretKey;
    }

    public boolean isPubNubUseSSL() {
        return pubNubUseSSL;
    }
}
