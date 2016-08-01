package com.ariel.guardian.command;

import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceApplicationService;

import java.util.ArrayList;
import java.util.List;

import ariel.commands.ApplicationCommands;
import ariel.commands.Param;

/**
 * Created by mikalackis on 6.7.16..
 */
public class ApplicationUpdate extends Command {

    @Override
    public void execute(final ArrayList<Param> params) {
        reportCommand("App update");
        Log.i("Application update", "Executing application update for package name: "+params);
        ArielGuardianApplication.getInstance().startService(DeviceApplicationService.getCallingIntent(params));
    }

}
