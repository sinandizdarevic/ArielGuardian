package com.ariel.guardian.command;

import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.services.DeviceLocationJobService;
import com.ariel.guardian.sync.SyncIntentService;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 16.11.16..
 */

public class ConfigurationCommand extends Command {

    protected ConfigurationCommand(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        Configuration deviceConfig = mGson.fromJson(message.getDataObject(), Configuration.class);
        mArielDatabase.createConfiguration(deviceConfig);
        mArielJobScheduler.registerNewJob(
                new DeviceLocationJobService(deviceConfig.getLocationTrackingInterval()));

        // set new ariel status which will check it based on observer in application class
        ArielSettings.Secure.putInt(mApplication.getContentResolver(),
                ArielSettings.Secure.ARIEL_SYSTEM_STATUS,
                deviceConfig.getArielSystemStatus());

        reportToMaster();
    }

    @Override
    protected void reportToMaster() {
        // cool, it worked, now report to the master
        if (message != null) {
            message.setOriginalMessageType(message.getMessageType());
            message.setMessageType(ArielConstants.MESSAGE_TYPE.REPORT);
            message.setSender(ArielUtilities.getUniquePseudoID());
            message.setExecuted(true);
            mArielDatabase.createWrapperMessage(message);
            mApplication.startService(SyncIntentService.getSyncIntent(message.getId()));
        }
    }

    public static class ConfigurationBuilder extends Command.AbstractBuilder<ConfigurationBuilder> {

        @Override
        protected ConfigurationBuilder me() {
            return this;
        }

        @Override
        public ConfigurationCommand build() {
            return new ConfigurationCommand(me());
        }

    }
}
