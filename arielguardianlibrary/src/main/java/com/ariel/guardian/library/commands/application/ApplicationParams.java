package com.ariel.guardian.library.commands.application;

import com.ariel.guardian.library.commands.Params;

/**
 * Application command available parameters
 */
public class ApplicationParams extends Params {

    // application params
    /**
     * Name of the application package that should be updated
     */
    public static final String PARAM_PACKAGE_NAME = "package_name";

    private final String packageName;

    public ApplicationParams(final ApplicationParamBuilder builder){
        super(ApplicationParams.class.getSimpleName());
        this.packageName = builder.packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * Application commands parameter builder class
     */
    public static class ApplicationParamBuilder{

        private String packageName;

        public ApplicationParamBuilder(final String appPackage){
            packageName = appPackage;
        }

        public ApplicationParams build(){
            return new ApplicationParams(this);
        }

    }

}
