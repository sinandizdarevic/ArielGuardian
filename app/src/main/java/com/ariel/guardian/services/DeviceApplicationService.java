package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.firebase.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.utils.Utilities;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceApplicationService extends ArielService implements DevicePackageValueEventListener.DataLoadCompletedListener{

    public static final String EXTRA_PACKAGE_NAME = "package_name";

    private final String TAG = "DeviceAppsService";

    private DatabaseReference mDeviceApplication;

    private DevicePackageValueEventListener mDevicePackageListener;

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
        Log.i(TAG, "Service onStart commmand with package: "+intent.getStringExtra(EXTRA_PACKAGE_NAME));
        mDeviceApplication = FirebaseHelper.getInstance().getFirebaseDatabase().getReference("application").child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(intent.getStringExtra(EXTRA_PACKAGE_NAME)));
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

    public static Intent getStartingIntent(final String params){
        Intent appService = new Intent(ArielGuardianApplication.getInstance(), DeviceApplicationService.class);
        /*
          For now, we support only one package update at a time. Reimplement this
          to support params having more then one package
         */
        appService.putExtra(DeviceApplicationService.EXTRA_PACKAGE_NAME, params);
        return appService;
    }

}
