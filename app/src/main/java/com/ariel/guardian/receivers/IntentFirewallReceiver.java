
package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;


public class IntentFirewallReceiver extends BroadcastReceiver {

    private static final String TAG = "IntentFirewallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("DeviceApplication blocked intent received: "+intent.getAction());
        String action = intent.getAction();
        if(action.equals("ariel.intent.action.BROADCAST_BLOCKED")){
            Logger.d("Brodacast blocked: "+action);
            //Toast.makeText(context, "You are not allowed to do this!", Toast.LENGTH_LONG).show();
        }
        else if(action.equals("ariel.intent.action.APPLICATION_BLOCKED")){
            Toast.makeText(context, "This application is blocked!", Toast.LENGTH_LONG).show();
        }
    }

}
