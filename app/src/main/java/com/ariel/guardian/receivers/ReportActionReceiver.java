
package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.commands.report.ReportCommands;
import com.ariel.guardian.library.commands.report.ReportParams;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import javax.inject.Inject;

public class ReportActionReceiver extends BroadcastReceiver {

    public static final String TAG = "ReportActionReceiver";

    public static final String PARAM_CHANNEL = "channel";
    public static final String PARAM_COMMAND = "command";
    public static final String PARAM_ERROR = "error";

    public static final String REPORT_COMMAND_ACTION = "report_command_execution";

    @Inject
    GuardianApplication mApplication;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Report action received");

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        String origin_channel = intent.getStringExtra(PARAM_CHANNEL);
        String command = intent.getStringExtra(PARAM_COMMAND);
        String error = intent.getStringExtra(PARAM_ERROR);

        Log.i(TAG, "Will report action complete on channel: "+origin_channel);

        CommandMessage cm = new CommandMessage(
                ReportCommands.REPORT_EXECUTION_COMMAND,
                new ReportParams.ReportParamBuilder()
                .invokedCommand(command)
                .commandStatus(error == null ? true : false)
                .errorMsg(error)
                .build());

//        mApplication.getPubNub().sendCommand(cm, new PNCallback<PNPublishResult>() {
//            @Override
//            public void onResponse(PNPublishResult result, PNStatus status) {
//
//            }
//        }, origin_channel);

    }

    public static Intent getReportActionIntent(final String channel, final String command, final String error){
        Intent intent = new Intent(REPORT_COMMAND_ACTION);
        intent.putExtra(PARAM_CHANNEL, channel);
        intent.putExtra(PARAM_COMMAND, command);
        intent.putExtra(PARAM_ERROR, error);
        return intent;
    }

}
