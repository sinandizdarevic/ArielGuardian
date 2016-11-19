package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.command.params.ConfigurationParams;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.services.DeviceLocationJobService;
import com.ariel.guardian.sync.SyncIntentService;

import javax.inject.Inject;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 16.11.16..
 */

public class ConfigurationCommand extends Command<ConfigurationParams> {

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Inject
    GuardianApplication mApplication;

    @Inject
    ArielJobScheduler mJobScheduler;

    protected ConfigurationCommand(AbstractBuilder builder) {
        super(builder);
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public void execute() {
        long configID = params.getConfigurationId();
        if (configID != -1) {
            Configuration config = mArielDatabase.getConfigurationByID(configID);
            mJobScheduler.registerNewJob(
                    new DeviceLocationJobService(config.getLocationTrackingInterval()));

            // set new ariel status which will check it based on observer in application class
            ArielSettings.Secure.putInt(mApplication.getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS,
                    config.getArielSystemStatus());
        }

        reportToMaster();
    }

    @Override
    protected void reportToMaster(){
        // cool, it worked, now report to the master
        if(message!=null) {
            message.setOriginalMessageType(message.getMessageType());
            message.setMessageType(ArielConstants.MESSAGES.REPORT);
            message.setSender(ArielUtilities.getUniquePseudoID());
            message.setExecuted(true);
            mArielDatabase.createWrapperMessage(message);
            mApplication.startService(SyncIntentService.getSyncIntent(message.getId()));
        }
    }

    public static class ConfigurationBuilder extends Command.AbstractBuilder<ConfigurationBuilder, ConfigurationParams> {

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
