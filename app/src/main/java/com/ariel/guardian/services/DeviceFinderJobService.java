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

package com.ariel.guardian.services;


import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.firebase.FirebaseHelper;
import com.ariel.guardian.model.DeviceLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class DeviceFinderJobService extends AntiTheftJobService implements LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = DeviceFinderJobService.class.getSimpleName();

    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final int MAX_LOCATION_UPDATES = 1;
    private static final int LOCATION_ACCURACY_THRESHOLD = 5; // meters
    private boolean mConstantReporting = false;

    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocationUpdate;
    private int mCurrentLocationmode = -1;

    private int mUpdateCount = 0;

    private boolean mIsRunning = false;

    private JobParameters mJobParams;

    private long locationUpdateInterval;

    public DeviceFinderJobService(){

    }

    public DeviceFinderJobService(final long locationUpdateInterval){
        this.locationUpdateInterval=locationUpdateInterval;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.mJobParams = jobParameters;
        if (!mIsRunning) {
            final Context context = getApplicationContext();
            mIsRunning = true;
            final ContentResolver contentResolver = getContentResolver();
            try {
                mCurrentLocationmode = Settings.Secure.getInt(contentResolver,
                        Settings.Secure.LOCATION_MODE);
                if (mCurrentLocationmode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                            Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            } catch (SettingNotFoundException e) {
                Log.e(TAG, "Unable find location settings.", e);
            }
//            int state = intent.getIntExtra(SERVICE_PARAM, Config.ANTITHEFT_STATE.NORMAL.getState());
//            if (state == Config.ANTITHEFT_STATE.THEFT.getState()) {
//                mConstantReporting = true;
//            }

            buildGoogleApiClient();
        }

        if (mGoogleApiClient.isConnected()) {
            restartLocationUpdates();
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mIsRunning = false;
        mGoogleApiClient.disconnect();
        return false;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private LocationRequest getLocationRequest() {
        LocationRequest lr = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL);
        if (!mConstantReporting) {
            lr.setNumUpdates(MAX_LOCATION_UPDATES);
        }
        return lr;
    }

    private void restartLocationUpdates() {
        mUpdateCount = 0;
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            onLocationChanged(lastLocation, true);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, getLocationRequest(), this);
    }

    @Override
    public void onLocationChanged(final Location location) {
        onLocationChanged(location, false);
    }

    private void onLocationChanged(final Location location, boolean fromLastLocation) {
        mLastLocationUpdate = location;
        if (!fromLastLocation)
            mUpdateCount++;
        DeviceLocation deviceLocation = new DeviceLocation(location.getTime(),location.getLatitude(),location.getLongitude());
        FirebaseHelper.getInstance().reportLocation(deviceLocation);
//        LocationParseObject locationObject = new LocationParseObject();
//        locationObject.setImei(DeviceInfo.getInstance().getIMEI());
//        locationObject.setLatitudeLongitude(location.getLatitude(), location.getLongitude());
//        locationObject.saveInBackground(new ParseSaveCallback("Location"));
        if (mLastLocationUpdate != null) {
            maybeStopLocationUpdates(mLastLocationUpdate.getAccuracy());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        restartLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void maybeStopLocationUpdates(float accuracy) {
        // if mUpdateCount, then this is a case we have the last known location. Don't stop in that
        // case.
        if (!mConstantReporting) {
            if ((mUpdateCount != 0)
                    && (accuracy <= LOCATION_ACCURACY_THRESHOLD || mUpdateCount == MAX_LOCATION_UPDATES)) {
                stopUpdates();
            }
        } else {
            Log.i(TAG, "Constant reporting in progress");
        }
    }

    public void stopUpdates() {
        // revert previous location settings
        final ContentResolver contentResolver = getContentResolver();
        Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                mCurrentLocationmode);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        jobFinished(mJobParams,false);
    }

    @Override
    public JobInfo getJobInfo() {
        ComponentName componentName = new ComponentName(ArielGuardianApplication.getInstance(), DeviceFinderJobService.class);
        return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOCATION.ordinal(), componentName).setPersisted(true).setPeriodic(locationUpdateInterval).build();
    }
}
