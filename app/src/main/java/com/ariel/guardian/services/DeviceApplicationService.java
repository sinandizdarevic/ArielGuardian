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
import com.ariel.guardian.firebase.listeners.DevicePackageValueEventListener;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceApplicationService extends ArielService implements DataLoadCompletedListener {

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
        super.onStartCommand(intent, flags, startId);
        mPackageName = intent.getStringExtra(ApplicationParams.PARAM_PACKAGE_NAME);
        mDeviceApplication = mFirebaseHelper.getFirebaseDatabase().getReference("application")
                .child(ArielUtilities.getUniquePseudoID())
                .child(ArielUtilities.encodeAsFirebaseKey(mPackageName));
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
        reportCommandExecuted(mInvoker,
                ApplicationCommands.APPLICATION_UPDATE_COMMAND,
                null);
        stopSelf();
    }

    @Override
    public void onDataLoadError(String errorMessage) {
        reportCommandExecuted(mInvoker,
                ApplicationCommands.APPLICATION_UPDATE_COMMAND,
                errorMessage);
        stopSelf();
    }

    public static Intent getCallingIntent(final ApplicationParams params){
        Intent appService = new Intent(GuardianApplication.getInstance(), DeviceApplicationService.class);
        appService.putExtra(ApplicationParams.PARAM_PACKAGE_NAME, params.getPackageName());
        appService.putExtra(ApplicationParams.PARAM_INVOKER, params.getInvoker());
        return appService;
    }

}
