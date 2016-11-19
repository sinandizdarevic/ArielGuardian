package com.ariel.guardian.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;



/**
 * Created by mikalackis on 8.6.16..
 */
abstract public class ArielService extends Service {

    public static final String TAG = "ArielService";

    private static PowerManager.WakeLock sWakeLock;

    protected String mInvoker;

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //mInvoker = intent.getStringExtra(Params.PARAM_INVOKER);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(getServiceName(),"onDestroy");
        if (sWakeLock != null && sWakeLock.isHeld()) {
            Logger.d(getServiceName(), "sWakeLock existing");
            sWakeLock.release();
        }
    }

    protected abstract String getServiceName();

    //public abstract void injectComponent(final GuardianComponent component);

}
