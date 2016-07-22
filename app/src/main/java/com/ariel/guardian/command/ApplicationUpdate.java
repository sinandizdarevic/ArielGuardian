package com.ariel.guardian.command;

import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceApplicationService;

import ariel.commands.ArielCommands;

/**
 * Created by mikalackis on 6.7.16..
 */
public class ApplicationUpdate implements CommandListener {

    public static final String APPLICATION_UPDATE_COMMAND = ArielCommands.APPLICATION_UPDATE_COMMAND;

    @Override
    public void execute(final String params) {
        Log.i("Application update", "Executing application update for package name: "+params);
        ArielGuardianApplication.getInstance().startService(DeviceApplicationService.getStartingIntent(params));
    }
}
