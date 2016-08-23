package com.ariel.guardian.library.commands.location;

import com.ariel.guardian.library.commands.Params;

/**
 * Created by mikalackis on 18.8.16..
 */
public class LocationParams extends Params {

    // command parameters
    /**
     * Boolean parameter. If provided, location will be reported via SMS too.
     * Not supported at the moment.
     */
    public static final String PARAM_SMS_LOCATION_REPORT = "sms_location_report";

    private boolean smsLocationReport;

    public LocationParams(final LocationParamBuilder builder){
        super(LocationParams.class.getSimpleName());
        this.smsLocationReport = builder.smsLocationReport;
    }

    public boolean getSmsLocationReport(){
        return smsLocationReport;
    }

    public static class LocationParamBuilder{

        private boolean smsLocationReport;

        public LocationParamBuilder smsLocationReport(final boolean report){
            smsLocationReport = report;
            return this;
        }

        public LocationParams build(){
            return new LocationParams(this);
        }

    }


}
