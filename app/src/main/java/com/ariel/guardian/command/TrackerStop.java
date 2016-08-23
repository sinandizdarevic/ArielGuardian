package com.ariel.guardian.command;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.services.DeviceFinderService;

import java.util.ArrayList;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStop extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("Stop tracking");
        ArielGuardianApplication.getInstance().stopService(DeviceFinderService.getCallingIntent(null));
    }
}
