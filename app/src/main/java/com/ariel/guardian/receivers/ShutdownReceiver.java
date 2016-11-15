
package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;



public class ShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = "ShutdownReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("Shutdown received, should send data ");

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

    }

}
