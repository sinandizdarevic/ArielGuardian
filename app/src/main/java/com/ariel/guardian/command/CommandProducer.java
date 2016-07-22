package com.ariel.guardian.command;

import com.ariel.guardian.utils.Utilities;

import java.util.HashMap;

/**
 * Created by mikalackis on 6.7.16..
 */
public class CommandProducer {

    private HashMap<String, CommandListener> mLocationCommandMap = new HashMap<>();
    private HashMap<String, CommandListener> mConfigCommandMap = new HashMap<>();
    private HashMap<String, CommandListener> mApplicationCommandMap = new HashMap<>();

    private HashMap<String, HashMap<String, CommandListener>> mChannelCommandMap = new HashMap<>();

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
        mLocationCommandMap.put(Locate.LOCATE_COMMAND, new Locate());
        mLocationCommandMap.put(TrackerStart.TRACK_START_COMMAND, new TrackerStart());
        mLocationCommandMap.put(TrackerStop.TRACK_STOP_COMMAND, new TrackerStop());

        mChannelCommandMap.put(Utilities.getPubNubLocationChannel(), mLocationCommandMap);
    }

    private void initConfigCommands() {
        mConfigCommandMap.clear();
        mConfigCommandMap.put(UpdateConfig.UPDATE_CONFIG_COMMAND, new UpdateConfig());
        mChannelCommandMap.put(Utilities.getPubNubConfigChannel(), mConfigCommandMap);
    }

    private void initApplicationCommands() {
        mApplicationCommandMap.clear();
        mApplicationCommandMap.put(ApplicationUpdate.APPLICATION_UPDATE_COMMAND, new ApplicationUpdate());

        mChannelCommandMap.put(Utilities.getPubNubApplicationChannel(), mApplicationCommandMap);
    }

    public void reinitialize() {
        mInstance = null;
        mInstance = new CommandProducer();
    }

    public CommandListener getCommand(final String channel, final String action) {
        return mChannelCommandMap.get(channel).get(action);
    }

    public CommandListener getLocationCommand(final String action) {
        return mLocationCommandMap.get(action);
    }

    public CommandListener getConfigCommand(final String action) {
        return mConfigCommandMap.get(action);
    }

}
