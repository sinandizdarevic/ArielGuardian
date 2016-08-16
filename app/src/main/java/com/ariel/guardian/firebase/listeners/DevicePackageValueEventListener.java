package com.ariel.guardian.firebase.listeners;

import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.model.DevicePackage;
import com.ariel.guardian.services.CreateIFRuleService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by mikalackis on 23.5.16..
 */
public class DevicePackageValueEventListener implements ValueEventListener {

    private static final String TAG = DevicePackageValueEventListener.class.getName();

    private DataLoadCompletedListener mListener;

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.i("DevicePackage", "onDataChange");
        DevicePackage devicePackage = dataSnapshot.getValue(DevicePackage.class);
        if (devicePackage != null) {
            Log.i("DevicePackage", "device package not null");
//            if (devicePackage.isDisabled()) {
//                Log.i("DevicePackage", "device package is disabled");
//                PackageManagerUtilities.killPackageProcess(ArielGuardianApplication.getInstance(), devicePackage.getPackageName());
//            }

            ArielGuardianApplication.getInstance().startService(CreateIFRuleService.getCallingIntent(devicePackage.getPackageName(), devicePackage.isDisabled()));

            if (mListener != null) {
                Log.i("DevicePackage", "notify listeners");
                mListener.onDataLoadCompleted();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
        if(mListener != null){
            mListener.onDataLoadCompleted();
        }
    }

    public void setDataLoadCompletedListener(final DataLoadCompletedListener listener) {
        mListener = listener;
    }

    public interface DataLoadCompletedListener {
        void onDataLoadCompleted();
    }
}
