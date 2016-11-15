package com.ariel.guardian.command;

import android.content.Context;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.application.ApplicationCommands;
import com.ariel.guardian.library.commands.configuration.DeviceConfigCommands;
import com.ariel.guardian.library.commands.location.LocationCommands;
import com.ariel.guardian.library.commands.location.LocationParams;

import java.util.HashMap;

import ariel.providers.ArielSettings;
import ariel.security.LockPatternUtilsHelper;

/**
 * Created by mikalackis on 6.7.16..
 */
public class CommandProducer {

//    private HashMap<String, Command> mLocationCommandMap = new HashMap<>();
//    private HashMap<String, Command> mConfigCommandMap = new HashMap<>();
//    private HashMap<String, Command> mApplicationCommandMap = new HashMap<>();

    private HashMap<String, Command> mChannelCommandMap = new HashMap<>();

    private Context mContext;

    public CommandProducer(final Context context) {
        this.mContext = context;
        mChannelCommandMap.clear();
        initChannelCommands();
    }

    private void initChannelCommands(){
        mChannelCommandMap.put(LocationCommands.LOCATE_NOW_COMMAND, new Locate());
        mChannelCommandMap.put(LocationCommands.TRACKING_START_COMMAND, new TrackerStart());
        mChannelCommandMap.put(LocationCommands.TRACKING_STOP_COMMAND, new TrackerStop());

        mChannelCommandMap.put(DeviceConfigCommands.ADD_MASTER_COMMAND, new AddMaster());
    }

    public void checkArielSystemStatus(final int arielSystemStatus){
        switch (arielSystemStatus) {
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_LOCKDOWN: {
                // // TODO: 29.7.16.
                /**
                 * Steps to do in case of LOCKDOWN mode:
                 * 1. Activate lockscreen
                 */
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_PANIC: {
                // // TODO: 29.7.16.
                /**
                 * Steps to do in case of PANIC mode:
                 * 1. Activate lockscreen
                 * 2. Start continuous location tracking
                 * 3. Keep reporting location to backend and SMS
                 * 4. Disable power button
                 * 5. Maybe activate camera face detection?
                 */

                // Lock the screen
                LockPatternUtilsHelper.performAdminLock("123qwe", mContext);

                // Start location tracking
                Command locationTracking = getCommand(LocationCommands.TRACKING_START_COMMAND);
                locationTracking.execute(new LocationParams.LocationParamBuilder().smsLocationReport(true).build());
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_THEFT: {
                // // TODO: 29.7.16.
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL: {
                // // TODO: 29.7.16.
                /**
                 * Steps to perform for NORMAL mode:
                 * 1. Restore previous lock screen and clear current one
                 * 2. Stop continuous location tracking
                 * 4. Enable power button
                 * 5. Deactivate camera face detection?
                 */

                // Clear screen lock
                LockPatternUtilsHelper.clearLock(mContext);

                // Stop location tracking
                Command locationTracking = getCommand(LocationCommands.TRACKING_STOP_COMMAND);
                locationTracking.execute(null);
                break;
            }
            default: {
                // // TODO: 29.7.16.
                break;
            }
        }
    }


    public Command getCommand(final String action) {
        return mChannelCommandMap.get(action);
    }

}
