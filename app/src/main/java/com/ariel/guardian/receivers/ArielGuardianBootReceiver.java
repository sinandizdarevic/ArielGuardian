
package com.ariel.guardian.receivers;

import com.ariel.guardian.firebase.FirebaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ArielGuardianBootReceiver extends BroadcastReceiver {

    private static final String TAG = "ArielGuardianBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boot received, should send data ");
        FirebaseHelper.getInstance().reportAction("Ariel system online");
    }

}
