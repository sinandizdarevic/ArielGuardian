package com.ariel.guardian.library.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 5.10.16..
 */

public class ArielDevice extends RealmObject {

    @PrimaryKey
    long id;

    private String deviceUID;

    private String arielChannel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceUID() {
        return deviceUID;
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public String getArielChannel() {
        return arielChannel;
    }

    public void setArielChannel(String arielChannel) {
        this.arielChannel = arielChannel;
    }
}
