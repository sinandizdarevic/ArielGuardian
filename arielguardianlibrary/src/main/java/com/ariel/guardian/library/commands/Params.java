package com.ariel.guardian.library.commands;

/**
 * Marker interface for command parameters
 */
public abstract class Params {

    /**
     * Type of Params class implementation, used by RuntimeTypeAdapterFactory and GSON
      */
    protected String type;

    protected Params(final String type){
        this.type = type;
    }

}