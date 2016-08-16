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

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.model.DeviceLocation;
import com.ariel.guardian.utils.LocationManager;
import com.google.android.gms.common.ConnectionResult;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import ariel.commands.LocationCommands;
import ariel.commands.Param;

public class DeviceFinderService extends ArielService implements LocationManager.LocationManagerListener {

    private static final String TAG = DeviceFinderService.class.getSimpleName();

    public static final String EXTRA_PARAM = "param";

    private boolean mIsRunning = false;

    private boolean mReportBySms = false;

    private LocationManager mLocationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service starting");
        if (!mIsRunning) {
            mLocationManager = new LocationManager.LocationManagerBuilder(this, this)
                    .locationAccuracyThreshold(5)
                    .constantReporting(true)
                    .locationUpdateInterval(5000)
                    .build();
            mLocationManager.initAndStartLocationUpdates();
        }

        mReportBySms = Boolean.parseBoolean(intent.getStringExtra(LocationCommands.PARAM_SMS_LOCATION_REPORT));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service stopped");
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
        return "DeviceFinderService";
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Got location: " + location.toString());
        DeviceLocation deviceLocation = new DeviceLocation(location.getTime(), location.getLatitude(), location.getLongitude());
        FirebaseHelper.getInstance().reportLocation(deviceLocation);
        if (mReportBySms) {
            // // TODO: 29.7.16. send SMS location with google maps URL
        }
    }

    @Override
    public void doneWithUpdates() {

    }

    public static Intent getCallingIntent(final ArrayList<Param> params) {
        Intent finderService = new Intent(ArielGuardianApplication.getInstance(), DeviceFinderService.class);
        if (params != null && params.size() > 0) {
            Iterator<Param> it = params.iterator();
            while (it.hasNext()) {
                Param param = it.next();
                finderService.putExtra(param.getParamName(), param.getValue().toString());
            }
        }
        return finderService;
    }

    @Override
    public void onGoogleClientError(ConnectionResult connectionResult) {
        stopSelf();
    }
}
