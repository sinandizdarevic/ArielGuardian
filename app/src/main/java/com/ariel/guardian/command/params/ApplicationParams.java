package com.ariel.guardian.command.params;

/**
 * DeviceApplication command available parameters
 */
public class ApplicationParams extends Params {

    private final String packageName;

    public ApplicationParams(final ApplicationParamBuilder builder){
        this.packageName = builder.packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * DeviceApplication commands parameter builder class
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
