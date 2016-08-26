package com.ariel.guardian.services;

import android.app.job.JobInfo;
import android.app.job.JobService;

import com.ariel.guardian.library.ArielLibrary;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.commands.report.ReportParams;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;


/**
 * Created by mikalackis on 23.5.16..
 */
public abstract class ArielJobService extends JobService {

    protected final String TAG = "ArielJobService";

    public abstract JobInfo getJobInfo();

    protected void reportCommandExecution(final ReportParams params, final String channel) {
//        CommandMessage cm = new CommandMessage(params.getInvokedCommand(), params);
//        ArielLibrary.action().sendCommand(cm, channel, new PNCallback<PNPublishResult>() {
//            @Override
//            public void onResponse(PNPublishResult result, PNStatus status) {
//            }
//        });
    }

}
