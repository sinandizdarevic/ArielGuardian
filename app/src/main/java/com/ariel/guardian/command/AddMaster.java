package com.ariel.guardian.command;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.commands.configuration.DeviceConfigCommands;
import com.ariel.guardian.library.commands.configuration.DeviceConfigParams;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.services.DeviceConfigService;

import java.util.ArrayList;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 6.7.16..
 */
public class AddMaster extends Command {

    @Override
    public void execute(final Params params) {
        reportCommand(DeviceConfigCommands.ADD_MASTER_COMMAND);
        // add new id to ariel secure settings
        ArrayList<String> masters =
                (ArrayList<String>)ArielSettings.Secure.getDelimitedStringAsList(
                GuardianApplication.getInstance().getContentResolver(),
                ArielSettings.Secure.ARIEL_MASTERS, ",");

        String masterId = ((DeviceConfigParams) params).getMasterId();

        if(!masters.contains(masterId)) {
            masters.add(masterId);
            ArielSettings.Secure.putListAsDelimitedString(
                    GuardianApplication.getInstance().getContentResolver(),
                    ArielSettings.Secure.ARIEL_MASTERS,
                    ",",
                    masters);
        }

        reportCommandExecuted(masterId,
                DeviceConfigCommands.ADD_MASTER_COMMAND,
                "");
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
