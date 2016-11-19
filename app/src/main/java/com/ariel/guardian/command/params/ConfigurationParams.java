package com.ariel.guardian.command.params;

/**
 * Created by mikalackis on 18.8.16..
 */
public class ConfigurationParams extends Params {

    private long configId;

    public ConfigurationParams(final ConfigParamBuilder builder){
        this.configId = builder.configId;
    }

    public long getConfigurationId(){
        return configId;
    }

    public static class ConfigParamBuilder{

        private long configId;

        public ConfigParamBuilder configurationId(final long id){
            configId = id;
            return this;
        }

        public ConfigurationParams build(){
            return new ConfigurationParams(this);
        }

    }


}
