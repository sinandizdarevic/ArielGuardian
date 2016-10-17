package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;

/**
 * Created by mikalackis on 7.6.16..
 */
public class UpdateDeviceAppsService extends ArielService {

    public static final String EXTRA_PARAM = "param";

    private final String TAG = "UpdateDeviceAppsService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        mDeviceApplication.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
//                while(it.hasNext()){
//                    DeviceApplication deviceApplication = it.next().getValue(DeviceApplication.class);
//                    Log.i(TAG, "Checking package: "+ deviceApplication.getPackageName());
//
//                    GuardianApplication.getInstance().
//                            startService(CreateIFRuleService.getCallingIntent
//                                    (deviceApplication.getPackageName(), deviceApplication.isDisabled()));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // failed, probably should reschedule
//            }
//        });
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

    public static Intent getCallingIntent(){
        Intent appService = new Intent(GuardianApplication.getInstance(), UpdateDeviceAppsService.class);
        return appService;
    }

}
