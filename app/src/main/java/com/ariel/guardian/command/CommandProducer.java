package com.ariel.guardian.command;

import com.ariel.guardian.utils.Utilities;

import java.util.HashMap;

import ariel.commands.ApplicationCommands;
import ariel.commands.DeviceConfigCommands;
import ariel.commands.LocationCommands;

/**
 * Created by mikalackis on 6.7.16..
 */
public class CommandProducer {

    private HashMap<String, Command> mLocationCommandMap = new HashMap<>();
    private HashMap<String, Command> mConfigCommandMap = new HashMap<>();
    private HashMap<String, Command> mApplicationCommandMap = new HashMap<>();

    private HashMap<String, HashMap<String, Command>> mChannelCommandMap = new HashMap<>();

    private static CommandProducer mInstance;

    public static synchronized CommandProducer getInstance() {
        if (mInstance == null) {
            mInstance = new CommandProducer();
        }
        return mInstance;
    }

    private CommandProducer() {
        mChannelCommandMap.clear();
        initLocationCommands();
        initConfigCommands();
        initApplicationCommands();
    }

    private void initLocationCommands() {
        mLocationCommandMap.clear();
        mLocationCommandMap.put(LocationCommands.LOCATE_NOW_COMMAND, new Locate());
        mLocationCommandMap.put(LocationCommands.TRACKING_START_COMMAND, new TrackerStart());
        mLocationCommandMap.put(LocationCommands.TRACKING_STOP_COMMAND, new TrackerStop());

        mChannelCommandMap.put(Utilities.getPubNubLocationChannel(), mLocationCommandMap);
    }

    private void initConfigCommands() {
        mConfigCommandMap.clear();
        mConfigCommandMap.put(DeviceConfigCommands.UPDATE_CONFIG_COMMAND, new UpdateConfig());
        mChannelCommandMap.put(Utilities.getPubNubConfigChannel(), mConfigCommandMap);
    }

    private void initApplicationCommands() {
        mApplicationCommandMap.clear();
        mApplicationCommandMap.put(ApplicationCommands.APPLICATION_UPDATE_COMMAND, new ApplicationUpdate());

        mChannelCommandMap.put(Utilities.getPubNubApplicationChannel(), mApplicationCommandMap);
    }

    public void reinitialize() {
        mInstance = null;
        mInstance = new CommandProducer();
    }

    public Command getCommand(final String channel, final String action) {
        return mChannelCommandMap.get(channel).get(action);
    }

    public Command getLocationCommand(final String action) {
        return mLocationCommandMap.get(action);
    }

    public Command getApplicationCommand(final String action) {
        return mApplicationCommandMap.get(action);
    }


    public Command getConfigCommand(final String action) {
        return mConfigCommandMap.get(action);
    }

}
