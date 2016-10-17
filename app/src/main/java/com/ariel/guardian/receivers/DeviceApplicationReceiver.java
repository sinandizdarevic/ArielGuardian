package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.realm.model.DeviceApplication;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.services.CreateIFRuleService;

/**
 * Created by mikalackis on 8.10.16..
 */

public class DeviceApplicationReceiver extends BroadcastReceiver {

    private static final String TAG = "DeviceApplicationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        long appId = intent.getLongExtra(ArielConstants.EXTRA_DATABASE_ID, -1);
        if (appId != -1) {
            DeviceApplication da = Ariel.action().database().getDeviceApplicationById(appId, false);

            Log.i(TAG, "Received an appID: "+appId+" with status: "+da.isDisabled());

            GuardianApplication.getInstance().
                    startService(CreateIFRuleService.getCallingIntent
                            (da.getPackageName(), da.isDisabled()));
        }

    }

}
