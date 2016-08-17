package com.ariel.guardian.library.commands;

/**
 * PoJO class that holds parameters used by commands
 */
public class Param {
    /**
     * Name of the parameters
     */
    final String paramName;
    /**
     * String representation of the parameter value
     */
    final String value;

    public Param(String paramName, String value) {
        this.paramName = paramName;
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public Object getValue() {
        return value;
    }

}