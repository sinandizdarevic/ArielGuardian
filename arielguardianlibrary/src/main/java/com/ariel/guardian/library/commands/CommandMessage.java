package com.ariel.guardian.library.commands;

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

    /**
     * List of parameters that go along with the command. Can be null.
     */
    private ArrayList<Param> params;

    public String getAction() {
        return action;
    }

    public ArrayList<Param> getParams() {
        return params;
    }

}
