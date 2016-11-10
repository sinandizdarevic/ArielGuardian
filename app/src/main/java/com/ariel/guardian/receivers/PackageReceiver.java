package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.services.CreateIFRuleService;
import com.ariel.guardian.sync.SyncIntentService;
import com.ariel.guardian.utils.PackageManagerUtilities;

import javax.inject.Inject;


/**
 * Created by mikalackis on 1.6.16..
 */
public class PackageReceiver extends BroadcastReceiver {

    private static final String TAG = PackageReceiver.class.getSimpleName();

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Package receiver activated: " + intent.getAction() + " packageName: " + intent.getData().getSchemeSpecificPart());

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        final String packageName = intent.getData().getSchemeSpecificPart();

        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            // check if app already exists in database
            DeviceApplication deviceApp = mArielDatabase.getApplicationByID(packageName);
            if (deviceApp != null) {
                // good, we had this app before
                // perform block check and set it to installed
                GuardianApplication.getInstance().
                        startService(CreateIFRuleService.getCallingIntent
                                (deviceApp.getPackageName(), deviceApp.isDisabled()));
                deviceApp.setUninstalled(false);
                mArielDatabase.createOrUpdateApplication(deviceApp);
                long id = mArielPubNub.createApplicationMessage(deviceApp, ArielConstants.ACTIONS.APPLICATION_ADDED, false);
                context.startService(SyncIntentService.getSyncIntent(id));
            }
            else{
                // this is new app, create it in the db
                final DeviceApplication deviceApplication = new DeviceApplication();

                deviceApplication.setPackageName(packageName);
                deviceApplication.setAppName(PackageManagerUtilities.getAppNameFromPackage(context, packageName));
                deviceApplication.setDisabled(false);
                deviceApplication.setUninstalled(false);

                mArielDatabase.createOrUpdateApplication(deviceApplication);
                long id = mArielPubNub.createApplicationMessage(deviceApplication, ArielConstants.ACTIONS.APPLICATION_ADDED, false);
                context.startService(SyncIntentService.getSyncIntent(id));
            }
        } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            // find the app in the database and update its uninstall status
            DeviceApplication deviceApp = mArielDatabase.getApplicationByID(packageName);
            if(deviceApp!=null) {
                deviceApp.setUninstalled(true);

                mArielDatabase.createOrUpdateApplication(deviceApp);
                long id = mArielPubNub.createApplicationMessage(deviceApp, ArielConstants.ACTIONS.APPLICATION_REMOVED, false);
                context.startService(SyncIntentService.getSyncIntent(id));
            }

        }
    }
}
