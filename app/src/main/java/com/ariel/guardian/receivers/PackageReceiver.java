package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ariel.guardian.database.DatabaseContract;
import com.ariel.guardian.database.DatabaseHelper;
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.firebase.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.model.DevicePackage;
import com.ariel.guardian.utils.PackageManagerUtilities;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

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

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(intent.getAction().equals("android.intent.action.PACKAGE_ADDED")){
            // INSTALLED PACKAGE
            devicePackage.setAppName(PackageManagerUtilities.getAppNameFromPackage(context,packageName));
            devicePackage.setInstalled(true);
            devicePackage.setDisabled(false);
            FirebaseHelper.getInstance().reportPackage(devicePackage, packageName);
            FirebaseHelper.getInstance().syncDevicePackageInformation(new DevicePackageValueEventListener(), packageName);

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
            FirebaseHelper.getInstance().removeDevicePackageListener(packageName);
            DatabaseReference dr = FirebaseHelper.getInstance().getAppPackageData(packageName);
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
