package com.ariel.guardian.model;

import com.ariel.guardian.firebase.FirebaseHelper;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by mikalackis on 23.5.16..
 */
@IgnoreExtraProperties
public class DeviceLocation {

    public String googleMapsUrl;
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
