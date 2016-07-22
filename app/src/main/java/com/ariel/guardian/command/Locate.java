package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.services.DeviceFinderJobService;

import ariel.commands.ArielCommands;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Locate implements CommandListener {

    public static final String LOCATE_COMMAND = ArielCommands.LOCATE_NOW_COMMAND;

    @Override
    public void execute(final String params) {
        ArielJobScheduler.getInstance().registerNewJob(new DeviceFinderJobService(-1));
    }
}
