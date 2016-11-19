package com.ariel.guardian.receivers.ariel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.services.DeviceLocationJobService;

import javax.inject.Inject;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 8.10.16..
 */

public class DeviceConfigReceiver extends BroadcastReceiver {

    private static final String TAG = "DeviceConfigReceiver";

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielJobScheduler mJobScheduler;

    @Override
    public void onReceive(Context context, Intent intent) {

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        long configID = intent.getLongExtra(ArielConstants.EXTRA_DATABASE_ID, -1);
        if (configID != -1) {
            Configuration config = mArielDatabase.getConfigurationByID(configID);
            mJobScheduler.registerNewJob(
                    new DeviceLocationJobService(config.getLocationTrackingInterval()));

            // set new ariel status which will check it based on observer in application class
            ArielSettings.Secure.putInt(context.getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS,
                    config.getArielSystemStatus());

        }

    }

}
