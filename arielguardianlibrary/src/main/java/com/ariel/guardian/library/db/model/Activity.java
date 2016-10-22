package com.ariel.guardian.library.db.model;

import com.orm.SugarRecord;

/**
 * Created by mikalackis on 5.10.16..
 */

public class Activity extends SugarRecord {

    /**
     * Action to be reported
     */
    public String action;
    /**
     * Timestamp when the action was performed
     */
    public long timestamp;

    public Activity(){}

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
}
