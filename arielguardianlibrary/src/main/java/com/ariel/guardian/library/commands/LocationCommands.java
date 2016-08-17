package com.ariel.guardian.library.commands;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class that contains commands related to Location services and a param builder
 * class.
 */
public final class LocationCommands {

    // location commands
    /**
     * Instant location
     */
    public static final String LOCATE_NOW_COMMAND = "locate";
    /**
     * Start location tracking
     */
    public static final String TRACKING_START_COMMAND = "track_start";
    /**
     * Stop location tracking
     */
    public static final String TRACKING_STOP_COMMAND = "track_stop";

    // command parameters
    /**
     * Boolean parameter. If provided, location will be reported via SMS too.
     * Not supported at the moment.
     */
    public static final String PARAM_SMS_LOCATION_REPORT = "sms_location_report";

    /**
     * Location command parameter builder class
     */
    public static class LocationParamBuilder{
        private ArrayList<Param> mParams;

        public LocationParamBuilder() {
            mParams = new ArrayList<>();
        }

        public LocationParamBuilder smsLocationReport(final boolean report){
            mParams.add(new Param(PARAM_SMS_LOCATION_REPORT, ""+report));
            return this;
        }

        public ArrayList<Param> build(){
            return mParams;
        }

    }

}
