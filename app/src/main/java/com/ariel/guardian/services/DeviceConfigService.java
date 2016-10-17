package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceConfigService extends ArielService{

    private final String TAG = "DeviceConfigService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Created DeviceConfigService");
        GuardianApplication.getInstance().getGuardianComponent().inject(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "DeviceConfigService destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onDataLoadCompleted() {
//        reportCommandExecuted(mInvoker,
//                DeviceConfigCommands.UPDATE_CONFIG_COMMAND,
//                null);
//        stopSelf();
//    }
//
//    @Override
//    public void onDataLoadError(String errorMessage) {
//        reportCommandExecuted(mInvoker,
//                DeviceConfigCommands.UPDATE_CONFIG_COMMAND,
//                errorMessage);
//        stopSelf();
//    }


    @Override
    String getServiceName() {
        return TAG;
    }

    public static Intent getCallingIntent(){
        Intent configService = new Intent(GuardianApplication.getInstance(), DeviceConfigService.class);
        return configService;
    }

}
