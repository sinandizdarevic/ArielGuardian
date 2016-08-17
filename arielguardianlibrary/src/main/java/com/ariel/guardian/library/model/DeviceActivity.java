package com.ariel.guardian.library.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * POJO class that represents activity object in Firebase database
 */
@IgnoreExtraProperties
public class DeviceActivity {

    /**
     * Action to be reported
     */
    public String action;
    /**
     * Timestamp when the action was performed
     */
    public long timestamp;
    /**
     * true/false if action was executed or not
     */
    public boolean actionExecuted;

    public DeviceActivity(){

    }

    public DeviceActivity(final String action, final long timestamp, final boolean actionExecuted){
        this.action = action;
        this.timestamp = timestamp;
        this.actionExecuted = actionExecuted;
    }

    public String getAction() {
        return action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isActionExecuted() {
        return actionExecuted;
    }
}
