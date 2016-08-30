package com.ariel.guardian.command;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.GuardianComponent;
import com.ariel.guardian.library.ArielLibrary;
import com.ariel.guardian.library.commands.Params;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.receivers.ReportActionReceiver;

import javax.inject.Inject;

/**
 * Created by mikalackis on 6.7.16..
 */
public abstract class Command {

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Inject
    ArielJobScheduler mJobScheduler;

    public Command(){
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
        //injectComponent(GuardianApplication.getInstance().getGuardianComponent());
    }

    public abstract void execute(final Params params);

    //public abstract void injectComponent(final GuardianComponent component);

    protected void reportCommand(String command){
        ArielLibrary.action().firebase().reportAction(command);
    }

    protected void reportCommandExecuted(String masterId, String command, String error){
        if(masterId!=null && masterId.length()>0) {
            Intent intent = ReportActionReceiver.getReportActionIntent(
                    ArielUtilities.getPubNubArielChannel(masterId),
                    command,
                    error);
            LocalBroadcastManager.getInstance(GuardianApplication.getInstance()).sendBroadcast(intent);
        }
        else{
            Log.i("COMMAND", "no master defined for this command");
        }
    }

}
