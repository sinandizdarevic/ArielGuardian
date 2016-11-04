package com.ariel.guardian;

import android.app.Application;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.command.CommandProducer;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.commands.location.LocationCommands;
import com.ariel.guardian.library.commands.location.LocationParams;
import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.library.utils.SharedPrefsManager;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.services.DeviceLocationJobService;
import com.ariel.guardian.sync.InstanceKeeperService;

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

        Log.i(TAG,"GuardianApplication app created");

        prepareDagger();

        if(SharedPrefsManager.getInstance(this).getBoolPreference(SharedPrefsManager.KEY_FIRST_RUN, true)){
            // this is the first run, setup config

            ArielDevice device = new ArielDevice();
            device.setId(Calendar.getInstance().getTimeInMillis());
            device.setDeviceUID(ArielUtilities.getUniquePseudoID());
            device.setArielChannel(ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
            mArielDatabase.createDevice(device);

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
            Log.i(TAG,"Ariel system status changed: " + arielSystemStatus);
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
                    LockPatternUtilsHelper.performAdminLock("123qwe", GuardianApplication.this);

                    // Start location tracking
                    Command locationTracking = CommandProducer.getInstance().getCommand(LocationCommands.TRACKING_START_COMMAND);
                    locationTracking.execute(new LocationParams.LocationParamBuilder().smsLocationReport(true).build());
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
                    LockPatternUtilsHelper.clearLock(GuardianApplication.this);

                    // Stop location tracking
                    Command locationTracking = CommandProducer.getInstance().getCommand(LocationCommands.TRACKING_STOP_COMMAND);
                    locationTracking.execute(null);
                    break;
                }
                default: {
                    // // TODO: 29.7.16.
                    break;
                }
            }
            Toast.makeText(GuardianApplication.this, "Ariel System status changed: " + arielSystemStatus, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
