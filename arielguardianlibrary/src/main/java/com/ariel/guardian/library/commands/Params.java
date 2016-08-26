package com.ariel.guardian.library.commands;

/**
 * Marker interface for command parameters
 */
public class Params {

    /**
     * Type of Params class implementation, used by RuntimeTypeAdapterFactory and GSON
      */
    public String type;

    protected Params(final String type){
        this.type = type;
    }

}