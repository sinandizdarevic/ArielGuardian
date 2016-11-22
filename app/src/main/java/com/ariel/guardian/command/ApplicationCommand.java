package com.ariel.guardian.command;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.services.CreateIFRuleService;
import com.orhanobut.logger.Logger;

/**
 * Created by mikalackis on 16.11.16..
 */

public final class ApplicationCommand extends Command {

    public ApplicationCommand(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        // parse the application from the message
        DeviceApplication deviceApp = mGson.fromJson(message.getDataObject(), DeviceApplication.class);
        mArielDatabase.createOrUpdateApplication(deviceApp);
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

    public static class ApplicationBuilder extends Command.AbstractBuilder<ApplicationBuilder> {
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
