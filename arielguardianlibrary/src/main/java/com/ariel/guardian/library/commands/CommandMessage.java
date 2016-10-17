package com.ariel.guardian.library.commands;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Plain POJO class used to hold data transferred between controlling device and
 * controlled device. Instance of this class represents a command to be executed with
 * additional parameters
 */
public class CommandMessage {

    /**
     * Action (command) to be executed on the device
     */
    private String action;

    private String invoker;

    /**
     * List of parameters that go along with the command. Can be null.
     */
    private Params params;

    public CommandMessage(final String action, final Params params){
        this.action = action;
        this.params = params;
    }

    public String getAction() {
        return action;
    }

    public Params getParams() {
        return params;
    }

}
