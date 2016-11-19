package com.ariel.guardian.command;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.command.params.ApplicationParams;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.services.CreateIFRuleService;
import com.ariel.guardian.sync.SyncIntentService;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

/**
 * Created by mikalackis on 16.11.16..
 */

public final class ApplicationCommand extends Command<ApplicationParams> {

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    GuardianApplication mApplication;

    protected ApplicationCommand(AbstractBuilder builder) {
        super(builder);
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public void execute() {
        DeviceApplication deviceApp = mArielDatabase.getApplicationByID(params.getPackageName());
        mApplication.
                startService(CreateIFRuleService.getCallingIntent
                        (deviceApp.getPackageName(), deviceApp.isDisabled(), new ApplicationProcessingReceiver(new Handler(Looper.getMainLooper()))));
    }

    public class ApplicationProcessingReceiver extends ResultReceiver {

        public ApplicationProcessingReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case CreateIFRuleService.ACTION_OK: {
                    if(next == null) {
                        Logger.i("No more commands, report");
                        reportToMaster();
                    }
                    else{
                        Logger.i("Move to next command");
                        next.execute();
                    }
                }
                case CreateIFRuleService.ACTION_FAILED: {
                    // crap, try the damn thing again
                    // or report to the master device with an error
                }
            }
        }
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

    public static class ApplicationBuilder extends Command.AbstractBuilder<ApplicationBuilder, ApplicationParams> {

        @Override
        protected ApplicationBuilder me() {
            return this;
        }

        @Override
        public ApplicationCommand build() {
            return new ApplicationCommand(me());
        }

    }

}
