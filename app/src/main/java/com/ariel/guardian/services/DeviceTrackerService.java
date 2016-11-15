package com.ariel.guardian.services;

/**
 * Created by mikalackis on 7.7.16..
 */
/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.location.LocationCommands;
import com.ariel.guardian.library.commands.location.LocationParams;
import com.ariel.guardian.utils.LocationManager;
import com.google.android.gms.common.ConnectionResult;
import com.orhanobut.logger.Logger;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;



public class DeviceTrackerService extends ArielService implements LocationManager.LocationManagerListener {

    private static final String TAG = DeviceTrackerService.class.getSimpleName();

    public static final String EXTRA_PARAM = "param";

    private boolean mIsRunning = false;

    private boolean mReportBySms = false;

    private LocationManager mLocationManager;

    @Inject
    GuardianApplication mApplication;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("Service starting");

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        if (!mIsRunning) {
            mLocationManager = new LocationManager.LocationManagerBuilder(this, this)
                    .locationAccuracyThreshold(5)
                    .constantReporting(true)
                    .locationUpdateInterval(5000)
                    .build();
            mLocationManager.initAndStartLocationUpdates();

        }

        mReportBySms = intent.getBooleanExtra(LocationParams.PARAM_SMS_LOCATION_REPORT, false);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("Service stopped");
        mIsRunning = false;
        mLocationManager.stopUpdates();
        mLocationManager = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    String getServiceName() {
        return "DeviceTrackerService";
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.d("Got location: " + location.toString());
        //DeviceLocation deviceLocation = new DeviceLocation(location.getTime(), location.getLatitude(), location.getLongitude());
        if (mReportBySms) {
            // // TODO: 29.7.16. send SMS location with google maps URL
        }
    }

    @Override
    public void doneWithUpdates() {

    }

    public static Intent getCallingIntent(final LocationParams params) {
        Intent finderService = new Intent(GuardianApplication.getInstance(), DeviceTrackerService.class);
        if(params!=null) {
            finderService.putExtra(LocationParams.PARAM_SMS_LOCATION_REPORT, params.getSmsLocationReport());
        }
        return finderService;
    }

    @Override
    public void onGoogleClientError(ConnectionResult connectionResult) {
        stopSelf();
    }
}
