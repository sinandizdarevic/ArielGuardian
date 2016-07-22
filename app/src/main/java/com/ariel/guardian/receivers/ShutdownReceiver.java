
package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ariel.guardian.firebase.FirebaseHelper;

import ariel.providers.ArielSettings;

public class ShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = "ShutdownReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Shutdown received, should send data ");
        FirebaseHelper.getInstance().reportAction("Ariel system offline");
    }

}
