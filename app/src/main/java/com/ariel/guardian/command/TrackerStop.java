package com.ariel.guardian.command;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceFinderService;

import java.util.ArrayList;
import java.util.List;

import ariel.commands.LocationCommands;
import ariel.commands.Param;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStop extends Command {

    @Override
    public void execute(final ArrayList<Param> params) {
        reportCommand("Stop tracking");
        ArielGuardianApplication.getInstance().stopService(DeviceFinderService.getCallingIntent(null));
    }
}
