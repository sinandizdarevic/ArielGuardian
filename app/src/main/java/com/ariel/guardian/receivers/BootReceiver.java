
package com.ariel.guardian.receivers;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.firebase.FirebaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Inject
    FirebaseHelper mFirebaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boot received, should send data ");

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        //((GuardianApplication)context.getApplicationContext()).getGuardianComponent().inject(this);

        mFirebaseHelper.reportAction("Ariel system online");
        //ArielSettings.Secure.putInt(context.getContentResolver(),ArielSettings.Secure.ARIEL_PROCESS_BLOCKER,1);
    }

}
