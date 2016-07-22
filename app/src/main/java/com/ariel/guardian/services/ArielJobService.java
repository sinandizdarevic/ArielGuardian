package com.ariel.guardian.services;

import android.app.job.JobInfo;
import android.app.job.JobService;

/**
 * Created by mikalackis on 23.5.16..
 */
public abstract class ArielJobService extends JobService {

    protected final String TAG = "ArielJobService";

    public abstract JobInfo getJobInfo();

}
