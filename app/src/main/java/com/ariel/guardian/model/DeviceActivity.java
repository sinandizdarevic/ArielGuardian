package com.ariel.guardian.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DeviceActivity {

    public String action;
    public long timestamp;
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
