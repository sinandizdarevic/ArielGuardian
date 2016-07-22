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
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.model.DeviceLocation;
import com.ariel.guardian.utils.LocationManager;
import com.google.android.gms.common.ConnectionResult;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class DeviceFinderService extends ArielService implements LocationManager.LocationManagerListener {

    private static final String TAG = DeviceFinderService.class.getSimpleName();

    private boolean mIsRunning = false;

    private LocationManager mLocationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            mLocationManager = new LocationManager.LocationManagerBuilder(this, this)
                    .locationAccuracyThreshold(5)
                    .constantReporting(true)
                    .locationUpdateInterval(5000)
                    .build();
            mLocationManager.initAndStartLocationUpdates();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsRunning = false;
        mLocationManager.stopUpdates();
        mLocationManager=null;
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
        Log.i(TAG, "Got location: "+location.toString());
        DeviceLocation deviceLocation = new DeviceLocation(location.getTime(),location.getLatitude(),location.getLongitude());
        FirebaseHelper.getInstance().reportLocation(deviceLocation);
    }

    @Override
    public void doneWithUpdates() {

    }

    public static Intent getStartingIntent(){
        Intent finderService = new Intent(ArielGuardianApplication.getInstance(), DeviceFinderService.class);
        return finderService;
    }

    @Override
    public void onGoogleClientError(ConnectionResult connectionResult) {
        stopSelf();
    }
}
