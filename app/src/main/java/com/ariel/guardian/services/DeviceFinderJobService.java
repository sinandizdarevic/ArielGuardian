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


import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.model.DeviceLocation;
import com.ariel.guardian.utils.LocationManager;
import com.google.android.gms.common.ConnectionResult;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.location.Location;
import android.util.Log;

import javax.inject.Inject;

public class DeviceFinderJobService extends ArielJobService implements LocationManager.LocationManagerListener {

    private static final String TAG = DeviceFinderJobService.class.getSimpleName();

    private boolean mIsRunning = false;

    private JobParameters mJobParams;

    private long locationUpdateInterval;

    private LocationManager mLocationManager;

    @Inject
    FirebaseHelper mFirebaseHelper;

    public DeviceFinderJobService(){
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    public DeviceFinderJobService(final long locationUpdateInterval){
        super();
        this.locationUpdateInterval=locationUpdateInterval;
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.mJobParams = jobParameters;
        if (!mIsRunning) {
            mLocationManager = new LocationManager.LocationManagerBuilder(this, this)
                                   .locationAccuracyThreshold(5)
                                   .maxLocationUpdates(1)
                                   .constantReporting(false)
                                   .locationUpdateInterval(5000)
                                   .build();
            mLocationManager.initAndStartLocationUpdates();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mIsRunning = false;
        mLocationManager.stopUpdates();
        mLocationManager = null;
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Got location: "+location.toString());
        DeviceLocation deviceLocation = new DeviceLocation(location.getTime(),location.getLatitude(),location.getLongitude());
        mFirebaseHelper.reportLocation(deviceLocation);
    }

    @Override
    public void doneWithUpdates() {
        jobFinished(mJobParams, false);
    }

    @Override
    public void onGoogleClientError(ConnectionResult connectionResult) {
        jobFinished(mJobParams, true);
    }

    @Override
    public JobInfo getJobInfo() {
        ComponentName componentName = new ComponentName(GuardianApplication.getInstance(), DeviceFinderJobService.class);
        if(locationUpdateInterval!=-1) {
            return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOCATION.ordinal(), componentName).setPersisted(true).setPeriodic(locationUpdateInterval).build();
        }
        else{
            return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOCATION_NOW.ordinal(), componentName).setMinimumLatency(1).setOverrideDeadline(2).build();
        }
    }
}
