package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.firebase.listeners.DataLoadCompletedListener;
import com.ariel.guardian.library.firebase.model.DeviceApplication;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by mikalackis on 7.6.16..
 */
public class UpdateDeviceAppsService extends ArielService implements DataLoadCompletedListener {

    public static final String EXTRA_PARAM = "param";

    private final String TAG = "UpdateDeviceAppsService";

    private DatabaseReference mDeviceApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service started");
        mDeviceApplication = mFirebaseHelper.getFirebaseDatabase()
                .getReference("application").child(ArielUtilities.getUniquePseudoID());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceApplication = null;
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDeviceApplication.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while(it.hasNext()){
                    DeviceApplication deviceApplication = it.next().getValue(DeviceApplication.class);
                    Log.i(TAG, "Checking package: "+ deviceApplication.getPackageName());

                    GuardianApplication.getInstance().
                            startService(CreateIFRuleService.getCallingIntent
                                    (deviceApplication.getPackageName(), deviceApplication.isDisabled()));
                }

                onDataLoadCompleted();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // failed, probably should reschedule
                onDataLoadError(databaseError.getMessage());
            }
        });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    String getServiceName() {
        return TAG;
    }

    @Override
    public void onDataLoadCompleted() {
        stopSelf();
    }

    @Override
    public void onDataLoadError(String errorMessage) {
        stopSelf();
    }

    public static Intent getCallingIntent(){
        Intent appService = new Intent(GuardianApplication.getInstance(), UpdateDeviceAppsService.class);
        return appService;
    }

}
