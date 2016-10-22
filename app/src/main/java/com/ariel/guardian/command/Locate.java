package com.ariel.guardian.command;

import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.services.DeviceLocationJobService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Locate extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("One shot location");
        mJobScheduler.registerNewJob(new DeviceLocationJobService(-1));
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }

}
