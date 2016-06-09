package com.ariel.guardian.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Intent;

import com.ariel.guardian.ArielGuardianApplication;
import com.ariel.guardian.ArielJobScheduler;

/**
 * Created by mikalackis on 7.6.16..
 */
public class RetryLoginJobService extends AntiTheftJobService {
    @Override
    public JobInfo getJobInfo() {
        ComponentName componentName = new ComponentName(ArielGuardianApplication.getInstance(), FirebaseAuthService.class);
        return new JobInfo.Builder(ArielJobScheduler.ArielJobID.LOGIN.ordinal(), componentName).setPersisted(true).setOverrideDeadline(60000).build();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent authService = new Intent(this, FirebaseAuthService.class);
        startService(authService);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
