package com.ariel.guardian.command;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.services.DeviceConfigService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class UpdateConfig extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("Config file update");
        ArielGuardianApplication.getInstance().startService(DeviceConfigService.getCallingIntent());
    }
}
