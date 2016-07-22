package com.ariel.guardian.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.ariel.guardian.ArielGuardianApplication;

/**
 * Created by mikalackis on 8.6.16..
 */
abstract public class ArielService extends Service {

    private static PowerManager.WakeLock sWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        if (sWakeLock == null) {
            PowerManager pm = (PowerManager)
                    ArielGuardianApplication.getInstance().getSystemService(Context.POWER_SERVICE);
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getServiceName());
        }
        if (!sWakeLock.isHeld()) {
            sWakeLock.acquire();
        }
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

}
