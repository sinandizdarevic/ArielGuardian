package com.ariel.guardian.library.database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 5.10.16..
 *
 * This class represents a master of an ArielDevice. Every time a device is added to
 * control application, this table should be populated
 */

public class ArielMaster extends RealmObject {

    @PrimaryKey
    private String deviceUID;

    public String getDeviceUID() {
        return deviceUID;
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
    }

}
