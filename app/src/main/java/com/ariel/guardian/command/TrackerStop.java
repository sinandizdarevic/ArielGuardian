package com.ariel.guardian.command;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.GuardianComponent;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.services.DeviceFinderService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStop extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("Stop tracking");
        GuardianApplication.getInstance().stopService(DeviceFinderService.getCallingIntent(null));
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
