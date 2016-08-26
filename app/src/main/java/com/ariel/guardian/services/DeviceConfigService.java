package com.ariel.guardian.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.firebase.listeners.DataLoadCompletedListener;
import com.ariel.guardian.firebase.listeners.DeviceConfigurationValueEventListener;
import com.ariel.guardian.library.commands.configuration.DeviceConfigCommands;
import com.ariel.guardian.library.commands.report.ReportParams;
import com.ariel.guardian.library.utils.Utilities;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceConfigService extends ArielService implements DataLoadCompletedListener{

    private final String TAG = "DeviceConfigService";

    private DatabaseReference mDeviceConfiguration;

    private DeviceConfigurationValueEventListener mDeviceConfigListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Created DeviceConfigService");
        ((GuardianApplication)getApplication()).getGuardianComponent().inject(this);

        mDeviceConfiguration = mFirebaseHelper.getFirebaseDatabase().getReference("configuration").child(Utilities.getUniquePsuedoID());
        mDeviceConfigListener = new DeviceConfigurationValueEventListener(mArielJobScheduler, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceConfiguration.removeEventListener(mDeviceConfigListener);
        mDeviceConfiguration = null;
        Log.i(TAG, "DeviceConfigService destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDeviceConfiguration.addListenerForSingleValueEvent(mDeviceConfigListener);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDataLoadCompleted() {
        reportCommandExecution(new ReportParams.ReportParamBuilder()
                .invokedCommand(DeviceConfigCommands.UPDATE_CONFIG_COMMAND)
                .commandStatus(true)
                .errorMsg(null)
                .build(), Utilities.getPubNubArielChannel(Utilities.getUniquePsuedoID()));
        stopSelf();
    }

    @Override
    public void onDataLoadError(String errorMessage) {
        reportCommandExecution(new ReportParams.ReportParamBuilder()
                .invokedCommand(DeviceConfigCommands.UPDATE_CONFIG_COMMAND)
                .commandStatus(false)
                .errorMsg(errorMessage)
                .build(), Utilities.getPubNubArielChannel(Utilities.getUniquePsuedoID()));
        stopSelf();
    }


    @Override
    String getServiceName() {
        return TAG;
    }

    public static Intent getCallingIntent(){
        Intent configService = new Intent(GuardianApplication.getInstance(), DeviceConfigService.class);
        return configService;
    }

}
