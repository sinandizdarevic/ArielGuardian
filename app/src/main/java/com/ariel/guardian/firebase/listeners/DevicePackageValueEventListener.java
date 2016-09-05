package com.ariel.guardian.firebase.listeners;

import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.firebase.model.DeviceApplication;
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
        Log.i("DeviceApplication", "onDataChange");
        DeviceApplication deviceApplication = dataSnapshot.getValue(DeviceApplication.class);
        if (deviceApplication != null) {
            Log.i("DeviceApplication", "device package not null");
//            if (deviceApplication.isDisabled()) {
//                Log.i("DeviceApplication", "device package is disabled");
//                PackageManagerUtilities.killPackageProcess(GuardianApplication.getInstance(), deviceApplication.getPackageName());
//            }

            GuardianApplication.getInstance().
                    startService(CreateIFRuleService.getCallingIntent
                            (deviceApplication.getPackageName(), deviceApplication.isDisabled()));

            if (mListener != null) {
                Log.i("DeviceApplication", "notify listeners");
                mListener.onDataLoadCompleted();
            }
        }
        else{
            mListener.onDataLoadError("Device package information was null");
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
        if(mListener != null){
            mListener.onDataLoadError(databaseError.getMessage());
        }
    }

    public void setDataLoadCompletedListener(final DataLoadCompletedListener listener) {
        mListener = listener;
    }

}
