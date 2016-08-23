package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.firebase.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.library.model.DevicePackage;
import com.ariel.guardian.utils.PackageManagerUtilities;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by mikalackis on 1.6.16..
 */
public class PackageReceiver extends BroadcastReceiver {

    private static final String TAG = PackageReceiver.class.getSimpleName();

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Package receiver activated: "+intent.getAction()+" packageName: "+intent.getData().getSchemeSpecificPart());

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        final String packageName = intent.getData().getSchemeSpecificPart();

        final DevicePackage devicePackage = new DevicePackage();
        devicePackage.setDate(Calendar.getInstance().getTimeInMillis());
        devicePackage.setPackageName(packageName);

        if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            // INSTALLED PACKAGE
            devicePackage.setAppName(PackageManagerUtilities.getAppNameFromPackage(context,packageName));
            devicePackage.setInstalled(true);
            devicePackage.setDisabled(false);
            mFirebaseHelper.reportPackage(devicePackage, packageName);
            mFirebaseHelper.syncDevicePackageInformation(new DevicePackageValueEventListener(), packageName);

            // write into internal db
            // UPDATE LOCAL VARIABLE THAT HOLDS DISABLED PACKAGES
//            ContentValues values = new ContentValues();
//            values.put(DatabaseContract.ApplicationEntry.COLUMN_NAME_APP_PACKAGE, packageName);
//            values.put(DatabaseContract.ApplicationEntry.COLUMN_NAME_APP_DISABLED, 989);
//
//            try {
//                ArielGuardian.ApplicationEntry.insertPackage(context.getContentResolver(), values);
//            } catch (ArielGuardian.ArielPackageNotFoundException e) {
//                e.printStackTrace();
//            }

//            long newRowId;
//            newRowId = db.insert(
//                    DatabaseContract.ApplicationEntry.TABLE_NAME,
//                    null,
//                    values);
        }
        else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){
            // REMOVED PACKAGE
            mFirebaseHelper.removeDevicePackageListener(packageName);
            DatabaseReference dr = mFirebaseHelper.getAppPackageData(packageName);
            dr.removeValue();

            //db.delete(DatabaseContract.ApplicationEntry.TABLE_NAME, DatabaseContract.ApplicationEntry.COLUMN_NAME_APP_PACKAGE + " = ?", new String[] { packageName });
        }
//        else if(intent.getAction().equals("android.intent.action.PACKAGE_CHANGED")){
//            // REMOVED PACKAGE
//            FirebaseHelper.getInstance().removeDevicePackageListener(packageName);
//            DatabaseReference dr = FirebaseHelper.getInstance().getAppPackageData(packageName);
//            dr.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    //Log.i(TAG, "DevicePackage data: "+dataSnapshot.getValue().toString());
//                    DevicePackage dp = dataSnapshot.getValue(DevicePackage.class);
//                    if(dp.isDisabled()){
//                        PackageManagerUtilities.setApplicationEnabledState(GuardianApplication.getInstance(), devicePackage.getPackageName(), devicePackage.isDisabled());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
    }
}
