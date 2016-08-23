package com.ariel.guardian.library.commands.location;

/**
 * Class that contains commands related to Location services and a param builder
 * class.
 */
public interface LocationCommands {

    // location commands
    /**
     * Instant location
     */
    String LOCATE_NOW_COMMAND = "locate";
    /**
     * Start location tracking
     */
    String TRACKING_START_COMMAND = "track_start";
    /**
     * Stop location tracking
     */
    String TRACKING_STOP_COMMAND = "track_stop";

}
