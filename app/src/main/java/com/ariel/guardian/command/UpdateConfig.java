package com.ariel.guardian.command;

import android.content.Intent;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceConfigService;

import ariel.commands.ArielCommands;

/**
 * Created by mikalackis on 6.7.16..
 */
public class UpdateConfig implements CommandListener {

    public static final String UPDATE_CONFIG_COMMAND = ArielCommands.UPDATE_CONFIG_COMMAND;

    @Override
    public void execute(final String params) {
        ArielGuardianApplication.getInstance().startService(DeviceConfigService.getStartingIntent());
    }
}
