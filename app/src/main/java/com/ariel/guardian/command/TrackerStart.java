package com.ariel.guardian.command;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.commands.location.LocationParams;
import com.ariel.guardian.services.DeviceTrackerService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStart extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("Start tracking");
        GuardianApplication.getInstance().startService(DeviceTrackerService.getCallingIntent((LocationParams)params));
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
