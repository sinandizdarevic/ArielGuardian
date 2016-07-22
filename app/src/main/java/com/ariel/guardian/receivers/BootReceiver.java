
package com.ariel.guardian.receivers;

import com.ariel.guardian.firebase.FirebaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ariel.providers.ArielSettings;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boot received, should send data ");
        FirebaseHelper.getInstance().reportAction("Ariel system online");
        //ArielSettings.Secure.putInt(context.getContentResolver(),ArielSettings.Secure.ARIEL_PROCESS_BLOCKER,1);
    }

}
