package com.ariel.guardian.firebase.listeners;

import android.util.Log;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.firebase.model.DeviceConfiguration;
import com.ariel.guardian.services.DeviceFinderJobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 25.8.16..
 */
public class DeviceConfigurationValueEventListener implements ValueEventListener {

    private static final String TAG = "DeviceConfEventListener";

    private ArielJobScheduler mArielJobScheduler;

    private DataLoadCompletedListener mListener;

    public DeviceConfigurationValueEventListener(final ArielJobScheduler mArielJobScheduler, final DataLoadCompletedListener listener){
        Log.i(TAG, "Initialized device configuration listener");
        this.mArielJobScheduler = mArielJobScheduler;
        this.mListener = listener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        final DeviceConfiguration deviceConfig = dataSnapshot.getValue(DeviceConfiguration.class);
        Log.i(TAG, "Received device configuration, ariel system status: "+deviceConfig.getArielSystemStatus());

        ArielSettings.Secure.putInt(GuardianApplication.getInstance().getContentResolver(),ArielSettings.Secure.ARIEL_SYSTEM_STATUS,deviceConfig.getArielSystemStatus());

        // update scheduled location tracking job
        mArielJobScheduler.cancelRunningJob(ArielJobScheduler.ArielJobID.LOCATION.ordinal());
        if(deviceConfig.getLocationTrackingInterval()>0) {
            Log.i(TAG, "Start device tracking job");
            mArielJobScheduler.registerNewJob(new DeviceFinderJobService(deviceConfig.getLocationTrackingInterval()));
        }

        if(mListener!=null){
            mListener.onDataLoadCompleted();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
        if(mListener!=null){
            mListener.onDataLoadError(databaseError.getMessage());
        }
    }

    public void setDataLoadCompletedListener(final DataLoadCompletedListener listener) {
        mListener = listener;
    }

}