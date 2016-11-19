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


import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.location.Location;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.sync.SyncIntentService;
import com.ariel.guardian.utils.LocationManager;
import com.google.android.gms.common.ConnectionResult;
import com.orhanobut.logger.Logger;

import java.util.Calendar;

import javax.inject.Inject;



public class DeviceLocationJobService extends ArielJobService implements LocationManager.LocationManagerListener {

    private static final String TAG = DeviceLocationJobService.class.getSimpleName();

    private boolean mIsRunning = false;

    private JobParameters mJobParams;

    private long locationUpdateInterval;

    private LocationManager mLocationManager;

    @Inject
    GuardianApplication mApplication;

    @Inject
    ArielDatabase mArielDatabase;

    @Inject
    ArielPubNub mArielPubNub;

    public DeviceLocationJobService(){
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    public DeviceLocationJobService(final long locationUpdateInterval){
        super();
        Logger.d("Define location job with interval: "+locationUpdateInterval);
        this.locationUpdateInterval=locationUpdateInterval;
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.mJobParams = jobParameters;
        Logger.d("Location job starting");
        if (!mIsRunning) {
            mLocationManager = new LocationManager.LocationManagerBuilder(this, this)
                                   .locationAccuracyThreshold(5)
                                   .maxLocationUpdates(1)
                                   .constantReporting(false)
                                   .locationUpdateInterval(5000)
                                   .build();
            mLocationManager.initAndStartLocationUpdates();
//            reportCommandExecution(new ReportParams.ReportParamBuilder()
//                    .invokedCommand(LocationCommands.LOCATE_NOW_COMMAND)
//                    .commandStatus(true)
//                    .errorMsg(null)
//                    .build(), ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
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
        Logger.d("Got location: "+location.toString());
        DeviceLocation loc = new DeviceLocation();
        loc.setLatitude(location.getLatitude());
        loc.setLongitude(location.getLongitude());
        loc.setTimestamp(Calendar.getInstance().getTimeInMillis());
        loc.setId(Calendar.getInstance().getTimeInMillis());
        loc.setGoogleMapsUrl(String.format(DeviceLocation.GOOGLE_MAPS_URL,
                location.getLatitude(), location.getLongitude()));
        mArielDatabase.createLocation(loc);
        long id = mArielPubNub.createLocationMessage(loc.getId(), ArielConstants.ACTIONS.LOCATION_UPDATE, false);
        mApplication.startService(SyncIntentService.getSyncIntent(id));
    }

    @Override
    public void doneWithUpdates() {
        jobFinished(mJobParams, true);
    }

    @Override
    public void onGoogleClientError(ConnectionResult connectionResult) {
//        reportCommandExecution(new ReportParams.ReportParamBuilder()
//                .invokedCommand(LocationCommands.LOCATE_NOW_COMMAND)
//                .commandStatus(false)
//                .errorMsg("Error starting google client")
//                .build(), ArielUtilities.getPubNubArielChannel(ArielUtilities.getUniquePseudoID()));
        jobFinished(mJobParams, true);
    }

    @Override
    public JobInfo getJobInfo() {
        ComponentName componentName = new ComponentName(mApplication, DeviceLocationJobService.class);
        if(locationUpdateInterval!=-1) {
            return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOCATION.ordinal(), componentName).setPersisted(true).setPeriodic(locationUpdateInterval).build();
        }
        else{
            return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOCATION_NOW.ordinal(), componentName).setMinimumLatency(1).setOverrideDeadline(2).build();
        }
    }
}
