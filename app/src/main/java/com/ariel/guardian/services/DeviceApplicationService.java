package com.ariel.guardian.services;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.firebase.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.utils.Utilities;
import com.google.firebase.database.DatabaseReference;

import java.util.Iterator;
import java.util.List;

import ariel.commands.ApplicationCommands;
import ariel.commands.Param;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceApplicationService extends ArielService implements DevicePackageValueEventListener.DataLoadCompletedListener{

    public static final String EXTRA_PARAM = "param";

    private final String TAG = "DeviceAppsService";

    private DatabaseReference mDeviceApplication;

    private DevicePackageValueEventListener mDevicePackageListener;

    private String mPackageName;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service started");
        mDevicePackageListener = new DevicePackageValueEventListener();
        mDevicePackageListener.setDataLoadCompletedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceApplication.removeEventListener(mDevicePackageListener);
        mDeviceApplication = null;
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPackageName = intent.getStringExtra(ApplicationCommands.PARAM_PACKAGE_NAME);
        mDeviceApplication = FirebaseHelper.getInstance().getFirebaseDatabase().getReference("application").child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(mPackageName));
        mDeviceApplication.addListenerForSingleValueEvent(mDevicePackageListener);
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

    public static Intent getCallingIntent(final List<Param> params){
        Intent appService = new Intent(ArielGuardianApplication.getInstance(), DeviceApplicationService.class);
        if (params != null && params.size() > 0) {
            Iterator<Param> it = params.iterator();
            while (it.hasNext()) {
                Param param = it.next();
                appService.putExtra(param.getParamName(), param.getValue().toString());
            }
        }
        return appService;
    }

}
