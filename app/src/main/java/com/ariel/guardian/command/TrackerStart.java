package com.ariel.guardian.command;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.commands.location.LocationParams;
import com.ariel.guardian.services.DeviceFinderService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStart extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("Start tracking");
        ArielGuardianApplication.getInstance().startService(DeviceFinderService.getCallingIntent((LocationParams)params));
    }
}
