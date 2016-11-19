package com.ariel.guardian.command.params;

/**
 * DeviceApplication command available parameters
 */
public class MasterParams extends Params {

    private final String masterId;

    public MasterParams(final MasterParamsBuilder builder){
        this.masterId = builder.masterId;
    }

    public String getMasterId() {
        return masterId;
    }

    /**
     * DeviceApplication commands parameter builder class
     */
    public static class MasterParamsBuilder{

        private String masterId;

        public MasterParamsBuilder(final String masterId){
            this.masterId = masterId;
        }

        public MasterParams build(){
            return new MasterParams(this);
        }

    }

}
