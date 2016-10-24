package com.ariel.guardian.library.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 5.10.16..
 */

public class Activity extends RealmObject {

    @PrimaryKey
    long id;

    /**
     * Action to be reported
     */
    public String action;
    /**
     * Timestamp when the action was performed
     */
    public long timestamp;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
