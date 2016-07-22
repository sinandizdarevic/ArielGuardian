package com.ariel.guardian;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ariel.guardian.services.DeviceConfigService;
import com.ariel.guardian.services.PubNubService;
import com.ariel.guardian.utils.Utilities;
import com.google.android.gms.common.api.GoogleApiClient;

import ariel.providers.ArielSettings;

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

        Log.i(TAG, "Calling anonym login for: "+ Utilities.getUniquePsuedoID());
//        Intent authService = new Intent(this, FirebaseAuthService.class);
//        startService(authService);
        startService(DeviceConfigService.getStartingIntent());

        ArielSettings.Secure.putString(getContentResolver(), ArielSettings.Secure.ARIEL_INTENT_FILTER_SERVICE, getString(R.string.intent_filter_service));

    }

    /**
     * Listen to changes on ariel system status and perform actions on change
     */
    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
            Toast.makeText(ArielGuardianApplication.getInstance(),"Ariel System status changed: "+arielSystemStatus, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
