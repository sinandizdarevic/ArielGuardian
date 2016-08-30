package com.ariel.guardian.library.commands;

/**
 * Marker interface for command parameters
 */
public class Params {

    public static final String PARAM_INVOKER = "invoker";

    /**
     * Type of Params class implementation, used by RuntimeTypeAdapterFactory and GSON
      */
    public String type;

    /**
     *
     * @param invoker person who invoked the command
     */
    public String invoker;

    protected Params(final String type){
        this.type = type;
    }

    public String getInvoker() {
        return invoker;
    }

    public void setInvoker(String invoker) {
        this.invoker = invoker;
    }
}