package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.services.DeviceFinderJobService;

import java.util.ArrayList;
import java.util.List;

import ariel.commands.LocationCommands;
import ariel.commands.Param;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Locate extends Command {

    @Override
    public void execute(final ArrayList<Param> params) {
        reportCommand("One shot location");
        ArielJobScheduler.getInstance().registerNewJob(new DeviceFinderJobService(-1));
    }
}
