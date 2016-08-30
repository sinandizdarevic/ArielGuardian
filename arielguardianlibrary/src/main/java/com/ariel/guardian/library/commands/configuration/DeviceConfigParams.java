package com.ariel.guardian.library.commands.configuration;

import com.ariel.guardian.library.commands.Params;

/**
 * Created by mikalackis on 18.8.16..
 */
public class DeviceConfigParams extends Params {

    // command parameters
    /**
     * Boolean parameter. If provided, location will be reported via SMS too.
     * Not supported at the moment.
     */
    public static final String PARAM_MASTER_ID = "master_id";

    private String masterId;

    public DeviceConfigParams(final ConfigParamBuilder builder){
        super(DeviceConfigParams.class.getSimpleName());
        this.masterId = builder.masterId;
    }

    public String getMasterId(){
        return masterId;
    }

    public static class ConfigParamBuilder{

        private String masterId;

        public ConfigParamBuilder masterId(final String id){
            masterId = id;
            return this;
        }

        public DeviceConfigParams build(){
            return new DeviceConfigParams(this);
        }

    }


}
