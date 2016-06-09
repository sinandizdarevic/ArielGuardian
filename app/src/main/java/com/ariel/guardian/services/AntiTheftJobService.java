package com.ariel.guardian.services;

import android.app.job.JobInfo;
import android.app.job.JobService;

/**
 * Created by mikalackis on 23.5.16..
 */
public abstract class AntiTheftJobService extends JobService {

    protected final String TAG = "AntiTheftJobService";

    public abstract JobInfo getJobInfo();

}
