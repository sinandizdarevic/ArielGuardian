package com.ariel.guardian;

import android.app.Application;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.widget.Toast;

import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.library.utils.SharedPrefsManager;
import com.ariel.guardian.services.DeviceLocationJobService;
import com.ariel.guardian.sync.InstanceKeeperService;
import com.orhanobut.logger.Logger;

import java.util.Calendar;

import javax.inject.Inject;

import ariel.providers.ArielSettings;
import ariel.security.LockPatternUtilsHelper;


/**
 * @author mikalackis
 */

public class GuardianApplication extends Application {

    public static final String TAG = "GuardianApplication";

    private GuardianComponent mGuardianComponent;

    private static GuardianApplication mInstance;

    @Inject
    ArielJobScheduler mJobScheduler;

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        Logger.init("ArielGuardian");

        Logger.i(TAG,"GuardianApplication app created");

        prepareDagger();

        if(SharedPrefsManager.getInstance(this).getBoolPreference(SharedPrefsManager.KEY_FIRST_RUN, true)){
            // this is the first run, setup config

            ArielDevice device = new ArielDevice();
            device.setId(Calendar.getInstance().getTimeInMillis());
            device.setDeviceUID(ArielUtilities.getUniquePseudoID());
            device.setArielChannel(ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
            mArielDatabase.createDevice(device);

            ArielMaster masterDevice1 = new ArielMaster();
            masterDevice1.setDeviceUID("00000000-738d-4550-ffff-ffffe203cca2");
            mArielDatabase.createOrUpdateMaster(masterDevice1);

            ArielMaster masterDevice2 = new ArielMaster();
            masterDevice2.setDeviceUID("00000000-4438-2cd8-ffff-fffffeacc937");
            mArielDatabase.createOrUpdateMaster(masterDevice2);

            Configuration configuration = new Configuration();
            configuration.setArielSystemStatus(getResources().getInteger(R.integer.ariel_system_status));
            configuration.setConstantTracking(getResources().getBoolean(R.bool.constant_location_tracking));
            configuration.setLocationTrackingInterval(getResources().getInteger(R.integer.location_tracking_interval));
            configuration.setActive(getResources().getBoolean(R.bool.configuration_active));
            configuration.setId(Calendar.getInstance().getTimeInMillis());
            mArielDatabase.createConfiguration(configuration);

            SharedPrefsManager.getInstance(this).setBooleanPrefernece(SharedPrefsManager.KEY_FIRST_RUN, false);

            //Ariel.action().pubnub().sendConfigurationMessage(id, ArielConstants.TYPE_DEVICE_CONFIG_UPDATE);
        }

//        int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
//                ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
//
//        mCommandProducer.checkArielSystemStatus(arielSystemStatus);

        getContentResolver().registerContentObserver(
                ArielSettings.Secure.getUriFor(ArielSettings.Secure.ARIEL_SYSTEM_STATUS),
                false, mSettingObserver);

        fireServices();

    }

    public static GuardianApplication getInstance(){
        return mInstance;
    }

    private void fireServices(){
        Configuration configuration = mArielDatabase.getActiveConfiguration();
        mJobScheduler.registerNewJob(new DeviceLocationJobService(configuration.getLocationTrackingInterval()));
        //mArielPubNub.subscribeToChannelsFromDB();

        // start instance keeper
        Intent instanceKeeper = new Intent(this, InstanceKeeperService.class);
        startService(instanceKeeper);

        mArielPubNub.subscribeToChannelsFromDB();

        //mArielPubNub.getMissedMessages();
    }

    private void prepareDagger() {
        mGuardianComponent = DaggerGuardianComponent.builder()
                .guardianModule(new GuardianModule(this))
                .build();
        mGuardianComponent.inject(GuardianApplication.this);
    }

    public GuardianComponent getGuardianComponent() {
        return mGuardianComponent;
    }

    /**
     * Listen to changes on ariel system status and perform actions on change
     */
    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
            Logger.d("Ariel system status changed: " + arielSystemStatus);
            checkArielSystemStatus(arielSystemStatus);
            Toast.makeText(GuardianApplication.this, "Ariel System status changed: " + arielSystemStatus, Toast.LENGTH_LONG).show();
        }
    };

    public void checkArielSystemStatus(final int arielSystemStatus){
        switch (arielSystemStatus) {
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_LOCKDOWN: {
                // // TODO: 29.7.16.
                /**
                 * Steps to do in case of LOCKDOWN mode:
                 * 1. Activate lockscreen
                 */
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_PANIC: {
                // // TODO: 29.7.16.
                /**
                 * Steps to do in case of PANIC mode:
                 * 1. Activate lockscreen
                 * 2. Start continuous location tracking
                 * 3. Keep reporting location to backend and SMS
                 * 4. Disable power button
                 * 5. Maybe activate camera face detection?
                 */

                // Lock the screen
                LockPatternUtilsHelper.performAdminLock("123qwe", this);

                // Start location tracking
//                Command locationTracking = getCommand(LocationCommands.TRACKING_START_COMMAND);
//                locationTracking.execute(new LocationParams.LocationParamBuilder().smsLocationReport(true).build());
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_THEFT: {
                // // TODO: 29.7.16.
                break;
            }
            case ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL: {
                // // TODO: 29.7.16.
                /**
                 * Steps to perform for NORMAL mode:
                 * 1. Restore previous lock screen and clear current one
                 * 2. Stop continuous location tracking
                 * 4. Enable power button
                 * 5. Deactivate camera face detection?
                 */

                // Clear screen lock
                LockPatternUtilsHelper.clearLock(this);

                // Stop location tracking
//                Command locationTracking = getCommand(LocationCommands.TRACKING_STOP_COMMAND);
//                locationTracking.execute(null);
                break;
            }
            default: {
                // // TODO: 29.7.16.
                break;
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
