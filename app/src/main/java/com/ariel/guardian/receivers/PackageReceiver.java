package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.model.DevicePackage;
import com.ariel.guardian.utils.PackageManagerUtilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikalackis on 1.6.16..
 */
public class PackageReceiver extends BroadcastReceiver {

    private static final String TAG = PackageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Package receiver activated: "+intent.getAction()+" packageName: "+intent.getData().getSchemeSpecificPart());
        final String packageName = intent.getData().getSchemeSpecificPart();

        final DevicePackage devicePackage = new DevicePackage();
        devicePackage.setDate(Calendar.getInstance().getTimeInMillis());
        devicePackage.setPackageName(packageName);

        if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            // INSTALLED PACKAGE
            devicePackage.setAppName(PackageManagerUtilities.getAppNameFromPackage(context,packageName));
            devicePackage.setInstalled(true);
            devicePackage.setDisabled(false);
            FirebaseHelper.getInstance().reportPackage(devicePackage, packageName);
            FirebaseHelper.getInstance().syncDevicePackageInformation(new DevicePackageValueEventListener(), packageName);
        }
        else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){
            // REMOVED PACKAGE
            FirebaseHelper.getInstance().removeDevicePackageListener(packageName);
            DatabaseReference dr = FirebaseHelper.getInstance().getAppPackageData(packageName);
            dr.removeValue();
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
//                        PackageManagerUtilities.setApplicationEnabledState(ArielGuardianApplication.getInstance(), devicePackage.getPackageName(), devicePackage.isDisabled());
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
