package com.ariel.guardian.library.utils;

/**
 * Created by mikalackis on 8.10.16..
 */

public class ArielConstants {

    /**
     * PubNub message types
     */
    public static String MESSAGE_TYPE_APPLICATION = "ariel.message.application";
    public static String MESSAGE_TYPE_LOCATION = "ariel.message.location";
    public static String MESSAGE_TYPE_CONFIGURATION = "ariel.message.configuration";

    /**
     * Application messages and intents
     */
    public static String TYPE_APPLICATION_ADDED = "ariel.intent.action.application.ADD";
    public static String TYPE_APPLICATION_REMOVED = "ariel.intent.action.application.REMOVE";
    public static String TYPE_APPLICATION_UPDATED = "ariel.intent.action.application.UPDATE";

    /**
     * Configuration messages and intents
     */
    public static String TYPE_DEVICE_CONFIG_UPDATE = "ariel.intent.action.device.CONFIG_UPDATE";

    /**
     * DeviceLocation messages and intents
     */
    public static String TYPE_LOCATION_UPDATE = "ariel.intent.action.location.UPDATE";

    /**
     * WrapperMessage messages and intents
     */
    public static String TYPE_WRAPPER_MESSAGE_REPORT = "ariel.intent.action.wrapper.REPORT";


    /**
     * Local broadcast extras
     */
    public static String EXTRA_DATABASE_ID = "database_id";




}
