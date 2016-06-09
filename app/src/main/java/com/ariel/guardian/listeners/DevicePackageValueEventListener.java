package com.ariel.guardian.listeners;

import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.model.DevicePackage;
import com.ariel.guardian.utils.PackageManagerUtilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mikalackis on 23.5.16..
 */
public class DevicePackageValueEventListener implements ValueEventListener{

    private static final String TAG = DevicePackageValueEventListener.class.getName();

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        DevicePackage devicePackage = dataSnapshot.getValue(DevicePackage.class);
        if(devicePackage!=null) {
            PackageManagerUtilities.setApplicationEnabledState(ArielGuardianApplication.getInstance(), devicePackage.getPackageName(), devicePackage.isDisabled());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
    }
}
