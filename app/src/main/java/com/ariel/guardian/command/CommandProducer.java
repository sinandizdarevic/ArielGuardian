package com.ariel.guardian.command;

import com.ariel.guardian.library.commands.application.ApplicationCommands;
import com.ariel.guardian.library.commands.configuration.DeviceConfigCommands;
import com.ariel.guardian.library.commands.location.LocationCommands;

import java.util.HashMap;

/**
 * Created by mikalackis on 6.7.16..
 */
public class CommandProducer {

//    private HashMap<String, Command> mLocationCommandMap = new HashMap<>();
//    private HashMap<String, Command> mConfigCommandMap = new HashMap<>();
//    private HashMap<String, Command> mApplicationCommandMap = new HashMap<>();

    private HashMap<String, Command> mChannelCommandMap = new HashMap<>();

    private static CommandProducer mInstance;

    public static synchronized CommandProducer getInstance() {
        if (mInstance == null) {
            mInstance = new CommandProducer();
        }
        return mInstance;
    }

    private CommandProducer() {
        mChannelCommandMap.clear();
        initChannelCommands();
    }

    private void initChannelCommands(){
        mChannelCommandMap.put(LocationCommands.LOCATE_NOW_COMMAND, new Locate());
        mChannelCommandMap.put(LocationCommands.TRACKING_START_COMMAND, new TrackerStart());
        mChannelCommandMap.put(LocationCommands.TRACKING_STOP_COMMAND, new TrackerStop());

        mChannelCommandMap.put(DeviceConfigCommands.ADD_MASTER_COMMAND, new AddMaster());

    }


    public void reinitialize() {
        mInstance = null;
        mInstance = new CommandProducer();
    }

    public Command getCommand(final String action) {
        return mChannelCommandMap.get(action);
    }

}
