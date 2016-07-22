package com.ariel.guardian.command;

import android.content.Intent;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.services.DeviceFinderService;

import ariel.commands.ArielCommands;

/**
 * Created by mikalackis on 6.7.16..
 */
public class TrackerStop implements CommandListener {

    public static final String TRACK_STOP_COMMAND = ArielCommands.TRACKING_STOP_COMMAND;

    @Override
    public void execute(final String params) {
        ArielGuardianApplication.getInstance().stopService(DeviceFinderService.getStartingIntent());
    }
}
