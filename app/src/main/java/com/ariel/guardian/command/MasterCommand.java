package com.ariel.guardian.command;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.command.params.MasterParams;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.sync.SyncIntentService;

import javax.inject.Inject;

/**
 * Created by mikalackis on 6.7.16..
 */
public class MasterCommand extends Command<MasterParams> {

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Inject
    GuardianApplication mApplication;

    @Inject
    ArielJobScheduler mJobScheduler;

    protected MasterCommand(AbstractBuilder builder) {
        super(builder);
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public void execute() {
        String masterId = params.getMasterId();
        ArielMaster masterDevice = new ArielMaster();
        masterDevice.setDeviceUID(masterId);
        mArielDatabase.createOrUpdateMaster(masterDevice);
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

    public static class MasterBuilder extends AbstractBuilder<MasterBuilder, MasterParams> {

        @Override
        protected MasterBuilder me() {
            return this;
        }

        @Override
        public MasterCommand build() {
            return new MasterCommand(me());
        }

    }

}
