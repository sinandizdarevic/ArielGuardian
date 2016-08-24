package com.ariel.guardian.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.library.firebase.model.DeviceConfiguration;
import com.ariel.guardian.library.pubnub.PubNubService;
import com.ariel.guardian.library.utils.Utilities;
import com.ariel.guardian.pubnub.listeners.ArielPubNubCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceConfigService extends ArielService {

    private final String TAG = "DeviceConfigService";

    private DatabaseReference mDeviceConfiguration;

    private DeviceConfigurationValueEventListener mDeviceConfigListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Created DeviceConfigService");
        ((GuardianApplication)getApplication()).getGuardianComponent().inject(this);

        mDeviceConfiguration = mFirebaseHelper.getFirebaseDatabase().getReference("configuration").child(Utilities.getUniquePsuedoID());
        mDeviceConfigListener = new DeviceConfigurationValueEventListener();
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
    String getServiceName() {
        return TAG;
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }

    public static Intent getCallingIntent(){
        Intent configService = new Intent(GuardianApplication.getInstance(), DeviceConfigService.class);
        return configService;
    }

    private class DeviceConfigurationValueEventListener implements ValueEventListener {

        private static final String TAG = "DeviceConfEventListener";

        public DeviceConfigurationValueEventListener(){
            Log.i(TAG, "Initialized device configuration listener");
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

            //EventBus.getDefault().post(new DeviceConfigEvent(deviceConfig, new ArielPubNubCallback()));

//            Intent intent = new Intent(GuardianApplication.getInstance(), PubNubService.class);
//            bindService(intent, new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                    Log.i(TAG, "Bound to PubNubService");
//                    PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) iBinder;
//                    PubNubService service = binder.getService();
//                    service.initPubNub(deviceConfig, new ArielPubNubCallback());
//                    unbindService(this);
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName componentName) {
//                    Log.i(TAG, "Unbound from PubNubService");
//                }
//            }, Context.BIND_AUTO_CREATE);

            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
            stopSelf();
        }
    }
}
