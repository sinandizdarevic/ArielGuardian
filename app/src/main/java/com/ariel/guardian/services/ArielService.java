package com.ariel.guardian.services;

import android.app.Service;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.ArielLibrary;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.commands.report.ReportParams;
import com.ariel.guardian.library.firebase.FirebaseHelper;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import javax.inject.Inject;

/**
 * Created by mikalackis on 8.6.16..
 */
abstract public class ArielService extends Service {

    private static PowerManager.WakeLock sWakeLock;

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Inject
    ArielJobScheduler mArielJobScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager)
                    GuardianApplication.getInstance().getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getServiceName());
        }
        if (!sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        //injectComponent(GuardianApplication.getInstance().getGuardianComponent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getServiceName(), "onDestroy");
        if (sWakeLock != null && sWakeLock.isHeld()) {
            Log.i(getServiceName(), "sWakeLock existing");
            sWakeLock.release();
        }
    }

    protected void reportCommandExecution(final ReportParams params, final String channel) {
//        CommandMessage cm = new CommandMessage(params.getInvokedCommand(), params);
//        ArielLibrary.action().sendCommand(cm, channel, new PNCallback<PNPublishResult>() {
//            @Override
//            public void onResponse(PNPublishResult result, PNStatus status) {
//
//            }
//        });
    }

    abstract String getServiceName();

    //public abstract void injectComponent(final GuardianComponent component);

}
