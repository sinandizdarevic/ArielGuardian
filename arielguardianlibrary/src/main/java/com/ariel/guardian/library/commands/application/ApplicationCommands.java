package com.ariel.guardian.library.commands.application;

/**
 * Class that contains commands for on device application management.
 */
public interface ApplicationCommands {

    // application commands
    /**
     * Command that informs the device that there was an update in single
     * application configuration and that the device should load new data.
     */
    String APPLICATION_UPDATE_COMMAND = "app_update";

}
