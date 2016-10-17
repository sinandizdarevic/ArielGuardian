package com.ariel.guardian.library.db.realm.model;

import io.realm.RealmObject;

/**
 * Created by mikalackis on 5.10.16..
 */

public class Location extends RealmObject {

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

    public String getGoogleMapsUrl() {
        return googleMapsUrl;
    }

    public void setGoogleMapsUrl(String googleMapsUrl) {
        this.googleMapsUrl = googleMapsUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
