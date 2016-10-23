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
public class DeviceLocation extends ArielModel {

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    @Column
    @PrimaryKey
    long id;

    /**
     * Link to google maps pointing to device location
     */
    @Column
    public String googleMapsUrl;
    /**
     * DeviceLocation timestamp
     */
    @Column
    public long timestamp;

    @Column
    public double latitude;

    @Column
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
