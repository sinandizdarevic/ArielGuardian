
package com.ariel.guardian.library.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * POJO class that represents device configuration object in firebase database
 */
@IgnoreExtraProperties
public class DeviceConfiguration {

    /**
     * Ariel systems status field
     */
    private int arielSystemStatus;
    /**
     * Phone number, can be null
     */
    private String phoneNumber;
    /**
     * Client (owner) id
     */
    private String clientId;
    /**
     * Device active flag (used not used)
     */
    private boolean active;
    /**
     * Time interval in which the device will upload its location to firebase
     */
    private long locationTrackingInterval;
    private String pubNubCipherKey;
    private String pubNubSubscribeKey;
    private String pubNubPublishKey;
    private String pubNubSecretKey;
    private boolean pubNubUseSSL;

    public DeviceConfiguration() {
        // default constructor required by firebase
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
