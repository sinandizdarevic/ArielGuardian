package com.ariel.guardian.library.commands;

import java.util.ArrayList;

/**
 * Class that contains commands for on device application management.
 */
public final class ApplicationCommands {

    // application commands
    /**
     * Command that informs the device that there was an update in single
     * application configuration and that the device should load new data.
     */
    public static final String APPLICATION_UPDATE_COMMAND = "app_update";

    // application params
    /**
     * Name of the application package that should be updated
     */
    public static final String PARAM_PACKAGE_NAME = "package_name";

    /**
     * Application commands parameter builder class
     */
    public static class ApplicationParamBuilder{
        private ArrayList<Param> params;

        public ApplicationParamBuilder(final String packageName){
            params = new ArrayList<Param>();
            params.add(new Param(PARAM_PACKAGE_NAME, packageName));
        }

        public ArrayList<Param> build(){
            return params;
        }

    }

}
