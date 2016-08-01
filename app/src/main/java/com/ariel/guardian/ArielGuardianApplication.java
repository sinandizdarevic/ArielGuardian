package com.ariel.guardian;

import android.app.Application;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.command.CommandProducer;
import com.ariel.guardian.command.TrackerStart;
import com.ariel.guardian.services.DeviceConfigService;
import com.ariel.guardian.services.PubNubService;
import com.ariel.guardian.utils.Utilities;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import ariel.commands.LocationCommands;
import ariel.commands.Param;
import ariel.providers.ArielSettings;
import ariel.security.LockPatternUtilsHelper;

/**
 * @author mikalackis
 */

public class ArielGuardianApplication extends Application {

    public static final String TAG = "ArielGuardianApp";

    private static ArielGuardianApplication mInstance;

    private GoogleApiClient mGoogleApiClient;

    public static ArielGuardianApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ArielGuardianApplication app created");

        mInstance = this;

//        DeviceInfo.getInstance().registerServiceStateListener();
//
        getContentResolver().registerContentObserver(
                ArielSettings.Secure.getUriFor(ArielSettings.Secure.ARIEL_SYSTEM_STATUS),
                false, mSettingObserver);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //FirebaseMessaging.getInstance().subscribeToTopic(Utilities.getConfigFCMTopic());

        Intent pubNubService = new Intent(ArielGuardianApplication.getInstance(), PubNubService.class);
        startService(pubNubService);

        Log.i(TAG, "Calling anonym login for: " + Utilities.getUniquePsuedoID());
//        Intent authService = new Intent(this, FirebaseAuthService.class);
//        startService(authService);
        startService(DeviceConfigService.getCallingIntent());

    }

    /**
     * Listen to changes on ariel system status and perform actions on change
     */
    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
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
                    LockPatternUtilsHelper.performAdminLock("123qwe", ArielGuardianApplication.this);

                    // Start location tracking
                    Command locationTracking = CommandProducer.getInstance().getLocationCommand(LocationCommands.TRACKING_START_COMMAND);
                    locationTracking.execute(new LocationCommands.LocationParamBuilder().smsLocationReport(true).build());
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

                    // Lock the screen
                    LockPatternUtilsHelper.clearLock(ArielGuardianApplication.this);

                    // Stop location tracking
                    Command locationTracking = CommandProducer.getInstance().getLocationCommand(LocationCommands.TRACKING_STOP_COMMAND);
                    locationTracking.execute(null);
                    break;
                }
                default: {
                    // // TODO: 29.7.16.
                }
            }
            Toast.makeText(ArielGuardianApplication.getInstance(), "Ariel System status changed: " + arielSystemStatus, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
