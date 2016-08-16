package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.library.eventbus.DeviceConfigEvent;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.model.DeviceConfiguration;
import com.ariel.guardian.pubnub.listeners.ArielPubNubCallback;
import com.ariel.guardian.utils.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import ariel.providers.ArielSettings;

/**
 * Created by mikalackis on 7.6.16..
 */
public class DeviceConfigService extends ArielService {

    private final String TAG = "FirebaseConfigService";

    private DatabaseReference mDeviceConfiguration;

    private DeviceConfigurationValueEventListener mDeviceConfigListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Created DeviceConfigService");
        mDeviceConfiguration = FirebaseHelper.getInstance().getFirebaseDatabase().getReference("configuration").child(Utilities.getUniquePsuedoID());
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

    public static Intent getCallingIntent(){
        Intent configService = new Intent(ArielGuardianApplication.getInstance(), DeviceConfigService.class);
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

            ArielSettings.Secure.putInt(ArielGuardianApplication.getInstance().getContentResolver(),ArielSettings.Secure.ARIEL_SYSTEM_STATUS,deviceConfig.getArielSystemStatus());

            // update scheduled location tracking job
            ArielJobScheduler.getInstance().cancelRunningJob(ArielJobScheduler.ArielJobID.LOCATION.ordinal());
            if(deviceConfig.getLocationTrackingInterval()>0) {
                Log.i(TAG, "Start device tracking job");
                ArielJobScheduler.getInstance().registerNewJob(new DeviceFinderJobService(deviceConfig.getLocationTrackingInterval()));
            }

            EventBus.getDefault().post(new DeviceConfigEvent(deviceConfig, new ArielPubNubCallback()));
            stopSelf();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.i(TAG, "loadConfig:onCancelled", databaseError.toException());
            stopSelf();
        }
    }
}
