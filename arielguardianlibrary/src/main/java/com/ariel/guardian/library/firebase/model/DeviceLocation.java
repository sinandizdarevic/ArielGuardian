package com.ariel.guardian.library.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * POJO class that represents device location object in firebase database
 */
@IgnoreExtraProperties
public class DeviceLocation {

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    /**
     * Link to google maps pointing to device location
     */
    public String googleMapsUrl;
    /**
     * Location timestamp
     */
    public long timestamp;
    public double latitude;
    public double longitude;

    public DeviceLocation(){

    }

    public DeviceLocation(final long timestamp,
                          final double latitude, final double longitude){
        this.timestamp=timestamp;
        this.latitude=latitude;
        this.longitude=longitude;
        googleMapsUrl = String.format(GOOGLE_MAPS_URL, latitude, longitude);
    }

}
