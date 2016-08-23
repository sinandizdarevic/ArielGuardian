package com.ariel.guardian.services;

import android.app.Service;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.ariel.guardian.ArielJobScheduler;
import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.GuardianComponent;
import com.ariel.guardian.library.firebase.FirebaseHelper;

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

    abstract String getServiceName();

    //public abstract void injectComponent(final GuardianComponent component);

}
