package com.ariel.guardian.command;

import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.commands.application.ApplicationParams;
import com.ariel.guardian.services.DeviceApplicationService;

/**
 * Created by mikalackis on 6.7.16..
 */
public class ApplicationUpdate extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand("App update");
        Log.i("Application update", "Executing application update for package name: "+params);
        ArielGuardianApplication.getInstance().startService(DeviceApplicationService.getCallingIntent((ApplicationParams)params));
    }

}
