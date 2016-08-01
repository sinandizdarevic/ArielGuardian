package com.ariel.guardian.command;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceConfigService;

import java.util.ArrayList;
import java.util.List;

import ariel.commands.DeviceConfigCommands;
import ariel.commands.Param;

/**
 * Created by mikalackis on 6.7.16..
 */
public class UpdateConfig extends Command {

    @Override
    public void execute(final ArrayList<Param> params) {
        reportCommand("Config file update");
        ArielGuardianApplication.getInstance().startService(DeviceConfigService.getCallingIntent());
    }
}
