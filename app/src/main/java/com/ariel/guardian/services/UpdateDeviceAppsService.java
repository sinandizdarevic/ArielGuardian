package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.firebase.listeners.DataLoadCompletedListener;
import com.ariel.guardian.library.commands.application.ApplicationCommands;
import com.ariel.guardian.library.commands.application.ApplicationParams;
import com.ariel.guardian.library.commands.report.ReportParams;
import com.ariel.guardian.library.firebase.model.DevicePackage;
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
                    DevicePackage devicePackage = it.next().getValue(DevicePackage.class);
                    Log.i(TAG, "Checking package: "+devicePackage.getPackageName());
                    /**
                     * This needs to be reimplemented: starting a service for every app
                     * that we get from firebase is quite heavy....
                     */
                    GuardianApplication.getInstance().
                            startService(CreateIFRuleService.getCallingIntent
                                    (devicePackage.getPackageName(), devicePackage.isDisabled()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // failed, probably should reschedule
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
