package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.services.DeviceFinderJobService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Locate extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("One shot location");
        ArielJobScheduler.getInstance().registerNewJob(new DeviceFinderJobService(-1));
    }

}
