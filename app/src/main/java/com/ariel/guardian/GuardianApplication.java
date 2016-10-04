package com.ariel.guardian;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ariel.guardian.command.Command;
import com.ariel.guardian.command.CommandProducer;
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.commands.configuration.DeviceConfigCommands;
import com.ariel.guardian.library.commands.configuration.DeviceConfigParams;
import com.ariel.guardian.library.commands.location.LocationCommands;
import com.ariel.guardian.library.commands.location.LocationParams;
import com.ariel.guardian.library.firebase.model.Settings;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.pubnub.PubNubService;
import com.ariel.guardian.pubnub.PubNubServiceInterface;
import com.ariel.guardian.pubnub.listeners.ArielPubNubCallback;
import com.ariel.guardian.receivers.ReportActionReceiver;
import com.ariel.guardian.services.DeviceConfigService;
import com.ariel.guardian.services.UpdateDeviceAppsService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import ariel.providers.ArielSettings;
import ariel.security.LockPatternUtilsHelper;

/**
 * @author mikalackis
 */

public class GuardianApplication extends Application {

    public static final String TAG = "GuardianApplication";

    private GuardianComponent mGuardianComponent;

    private PubNubServiceInterface mPubNubService;

    private static GuardianApplication mInstance;

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "GuardianApplication app created");

        mInstance = this;

        getContentResolver().registerContentObserver(
                ArielSettings.Secure.getUriFor(ArielSettings.Secure.ARIEL_SYSTEM_STATUS),
                false, mSettingObserver);

        getContentResolver().registerContentObserver(
                ArielSettings.Secure.getUriFor(ArielSettings.Secure.ARIEL_MASTERS),
                false, mMastersObserver);

        prepareDagger();

        registerCommandReportReceiver();

        fetchFirebaseSettings();

        Log.i(TAG, "Calling anonym login for: " + ArielUtilities.getUniquePseudoID());
//        Intent authService = new Intent(this, FirebaseAuthService.class);
//        startService(authService);

    }

    public static GuardianApplication getInstance(){
        return mInstance;
    }

    private void fetchFirebaseSettings(){
        AsyncTask.execute(new Runnable() {
                              @Override
                              public void run() {
                                  DatabaseReference settingsRef = mFirebaseHelper.getSettingsReference();
                                  settingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                          Log.i(TAG, "Received firebase settings");
                                          Settings settings = dataSnapshot.getValue(Settings.class);
                                          if (settings != null) {
                                              Log.i(TAG, "Starting pubnub service");
                                              initiatePubNubService(settings);
                                          }
                                      }

                                      @Override
                                      public void onCancelled(DatabaseError databaseError) {
                                          Log.i(TAG, "Database error: " + databaseError.getDetails() + ", " + databaseError.getMessage());
                                      }
                                  });
                              }
                          }
        );
    }

    public PubNubServiceInterface getPubNub(){
        return mPubNubService;
    }

    private void initiatePubNubService(final Settings settings){

        Intent pubNubServiceIntent = new Intent(GuardianApplication.this, PubNubService.class);
        pubNubServiceIntent.putExtra(PubNubService.EXTRA_PUBNUB_CIPHER_KEY, settings.getPubNubCipherKey());
        pubNubServiceIntent.putExtra(PubNubService.EXTRA_PUBNUB_PUBLISH_KEY, settings.getPubNubPublishKey());
        pubNubServiceIntent.putExtra(PubNubService.EXTRA_PUBNUB_SECRET_KEY, settings.getPubNubSecretKey());
        pubNubServiceIntent.putExtra(PubNubService.EXTRA_PUBNUB_SUBSCRIBE_KEY, settings.getPubNubSubscribeKey());

        startService(pubNubServiceIntent);

        bindService(pubNubServiceIntent, mConnection, BIND_AUTO_CREATE);

        fireServices();
    }

    private void fireServices(){
        startService(DeviceConfigService.getCallingIntent());
        startService(UpdateDeviceAppsService.getCallingIntent());
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service bound!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) service;
            mPubNubService = binder.getService();

            mPubNubService.subscribeToChannelsWithCallback(new ArielPubNubCallback(),
                                                      ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    public String[] getMasterChannels() {
        ArrayList<String> masters = (ArrayList<String>) ArielSettings.Secure.getDelimitedStringAsList(getContentResolver(),
                ArielSettings.Secure.ARIEL_MASTERS, ",");
        String[] channels = new String[masters.size()];
        Iterator<String> it = masters.iterator();
        int i = 0;
        while (it.hasNext()) {
            channels[i] = ArielUtilities.getPubNubArielChannel(it.next());
            i++;
        }
        return channels;
    }

//    private void registerArielLibraryReceiver() {
//        LocalBroadcastManager.getInstance(this)
//                .registerReceiver(new BroadcastReceiver() {
//                                      @Override
//                                      public void onReceive(Context context, Intent intent) {
//                                          Log.i(TAG, "RECEIVE LOCAL BROADCAST " + intent.getAction());
//                                          String action = intent.getAction();
//                                          if (action.equals(ArielLibrary.BROADCAST_LIBRARY_READY)) {
//                                              // fetch device config
//                                              startService(DeviceConfigService.getCallingIntent());
//
//                                              /**
//                                               * Subscribe to two channels:
//                                               * 1. ariel channel - to distribute messages to all members
//                                               * 2. device specific channel - to receive commands
//                                               */
//                                              ArielLibrary.action().subscribeToChannelsWithCallback(new ArielPubNubCallback(),
//                                                      ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
//
//                                              // sync apps list on firebase side and check status
//                                              startService(UpdateDeviceAppsService.getCallingIntent());
//
//                                          }
//                                      }
//                                  },
//                        new IntentFilter(ArielLibrary.BROADCAST_LIBRARY_READY));
//    }

    private void registerCommandReportReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new ReportActionReceiver(),
                new IntentFilter(ReportActionReceiver.REPORT_COMMAND_ACTION));
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
     * Listen to changes on ariel system masters
     */
    private final ContentObserver mMastersObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            ArrayList<String> masters = (ArrayList<String>) ArielSettings.Secure.getDelimitedStringAsList(getContentResolver(),
                    ArielSettings.Secure.ARIEL_MASTERS, ",");
            try {
                mPubNubService.subscribeToChannels((String[]) masters.toArray());
            } catch (RuntimeException re) {

            }
        }
    };

    /**
     * Listen to changes on ariel system status and perform actions on change
     */
    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            int arielSystemStatus = ArielSettings.Secure.getInt(getContentResolver(),
                    ArielSettings.Secure.ARIEL_SYSTEM_STATUS, ArielSettings.Secure.ARIEL_SYSTEM_STATUS_NORMAL);
            Log.i(TAG, "Ariel system status changed: " + arielSystemStatus);
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
