package com.ariel.guardian;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.util.Log;

import com.ariel.guardian.services.ArielJobService;

import java.util.Iterator;

/**
 * Created by mikalackis on 23.5.16..
 */
public class ArielJobScheduler {

    public static enum ArielJobID{
        LOCATION,
        LOCATION_NOW,
        LOGIN
    }

    private static ArielJobScheduler mArielJobScheduler;

    private JobScheduler mJobScheduler;

    public ArielJobScheduler(final Context context){
        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public void registerNewJob(final ArielJobService service){
        Log.i("ArielJobScheduler", "JOB SCHEDULED");
        mJobScheduler.schedule(service.getJobInfo());
    }

    public void getPendingJobs(){
        Iterator<JobInfo> it = mJobScheduler.getAllPendingJobs().iterator();
        while(it.hasNext()){
            JobInfo ji = it.next();
            Log.i("ArielJobScheduler", "JOB INFO: "+ji.toString());
        }
    }

    public void cancelRunningJob(final int jobId){
        mJobScheduler.cancel(jobId);
    }

    public void cancelRunningJobs(final int... jobs){
        for(int i=0;i<jobs.length;i++){
            mJobScheduler.cancel(jobs[i]);
        }
    }

}
