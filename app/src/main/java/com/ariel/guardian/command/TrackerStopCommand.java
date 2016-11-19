package com.ariel.guardian.command;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.services.DeviceTrackerService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStopCommand extends Command {

    public TrackerStopCommand() {
        super(null);
    }

    @Override
    public void execute() {
        GuardianApplication.getInstance().stopService(DeviceTrackerService.getCallingIntent(null));
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
