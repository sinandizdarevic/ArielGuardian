package com.ariel.guardian.library.commands.configuration;

/**
 * Class that contains commands for device configuration
 */
public interface DeviceConfigCommands {

    // config commands
    /**
     * Activate device configuration update. Not active at the moment
     */
    String UPDATE_CONFIG_COMMAND = "update_config";

    /**
     * Report device manager information
     */
    String ADD_MASTER_COMMAND = "add_master";

//    protected enum PARAMS{
//        SMS_REPORT("sms_location_report");
//
//        private String param;
//        PARAMS(String param){
//            this.param = param;
//        }
//
//        public String getParam(){
//            return param;
//        }
//    }

}
