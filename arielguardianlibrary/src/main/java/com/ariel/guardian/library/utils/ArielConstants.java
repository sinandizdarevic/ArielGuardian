package com.ariel.guardian.library.utils;

/**
 * Created by mikalackis on 8.10.16..
 */

public final class ArielConstants {

    public interface ACTION {
        /**
         * Application messages and intents
         */
        String APPLICATION_ADDED = "ariel.intent.action.application.ADD";
        String APPLICATION_REMOVED = "ariel.intent.action.application.REMOVE";
        String APPLICATION_UPDATED = "ariel.intent.action.application.UPDATE";

        /**
         * Configuration messages and intents
         */
        String DEVICE_CONFIG_UPDATE = "ariel.intent.action.device.CONFIG_UPDATE";
        String GET_DEVICE_QR_CODE = "ariel.intent.action.device.QR_CODE";
        String ADD_MASTER_DEVICE = "ariel.intent.action.device.ADD_MASTER";

        /**
         * DeviceLocation messages and intents
         */
        String LOCATION_UPDATE = "ariel.intent.action.location.UPDATE";

    }

    public interface MESSAGE_TYPE {
        /**
         * PubNub message types
         */
        String APPLICATION = "ariel.message.application";
        String LOCATION = "ariel.message.location";
        String CONFIGURATION = "ariel.message.configuration";
        String REPORT = "ariel.message.report";
    }


    /**
     * Local broadcast extras
     */
    public static String EXTRA_DATABASE_ID = "database_id";
    public static String EXTRA_WRAPPER_ID = "wrapper_id";




}
