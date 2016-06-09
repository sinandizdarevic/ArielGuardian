package com.ariel.guardian;

import android.app.job.JobScheduler;
import android.content.Context;

import com.ariel.guardian.services.AntiTheftJobService;

/**
 * Created by mikalackis on 23.5.16..
 */
public class ArielJobScheduler {

    public static enum ArielJobID{
        LOCATION,
        LOGIN
    }

    private static ArielJobScheduler mArielJobScheduler;

    private JobScheduler mJobScheduler;

    public static ArielJobScheduler getInstance(){
        if(mArielJobScheduler==null){
            mArielJobScheduler = new ArielJobScheduler();
        }
        return mArielJobScheduler;
    }

    private ArielJobScheduler(){
        mJobScheduler = (JobScheduler) ArielGuardianApplication.getInstance().getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public void registerNewJob(final AntiTheftJobService service){
        mJobScheduler.schedule(service.getJobInfo());
    }

    public void cancelRunningJob(final int jobId){
        mJobScheduler.cancel(jobId);
    }

}
