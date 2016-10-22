package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.utils.PackageManagerUtilities;

import java.util.Calendar;

/**
 * Created by mikalackis on 1.6.16..
 */
public class PackageReceiver extends BroadcastReceiver {

    private static final String TAG = PackageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Package receiver activated: "+intent.getAction()+" packageName: "+intent.getData().getSchemeSpecificPart());

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        final String packageName = intent.getData().getSchemeSpecificPart();

        final DeviceApplication deviceApplication = new DeviceApplication();

        deviceApplication.setPackageName(packageName);

        if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            // INSTALLED PACKAGE
            deviceApplication.setAppName(PackageManagerUtilities.getAppNameFromPackage(context,packageName));
            deviceApplication.setId(Calendar.getInstance().getTimeInMillis());
            deviceApplication.setDisabled(false);

            long applicationId = Ariel.action().database().createOrUpdateObject(deviceApplication);
            Ariel.action().pubnub().sendApplicationMessage(packageName, ArielConstants.TYPE_APPLICATION_ADDED);

        }
        else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){
            // REMOVED PACKAGE
            //mFirebaseHelper.removeDevicePackageListener(packageName);

            DeviceApplication deviceApp = (DeviceApplication)Ariel.action().database()
                    .getObjectByField(DeviceApplication.class, "package_name", packageName);

            Ariel.action().database().removeObject(deviceApp);

            Ariel.action().pubnub().sendApplicationMessage(packageName, ArielConstants.TYPE_APPLICATION_REMOVED);

            //db.delete(DatabaseContract.ApplicationEntry.TABLE_NAME, DatabaseContract.ApplicationEntry.COLUMN_NAME_APP_PACKAGE + " = ?", new String[] { packageName });
        }
    }
}
