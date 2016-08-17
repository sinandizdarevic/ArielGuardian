package com.ariel.guardian.library.model;

import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * POJO class that represents device location object in firebase database
 */
@IgnoreExtraProperties
public class DeviceLocation {

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
        googleMapsUrl = String.format(FirebaseHelper.GOOGLE_MAPS_URL, latitude, longitude);
    }

}
