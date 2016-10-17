package com.ariel.guardian.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.ariel.guardian.GuardianApplication;

/**
 * Created by mikalackis on 17.10.16..
 */

public class IntentFirewallService extends Service {

    protected final static String TAG = "IntentFirewallService";

    protected final static int CREATE_RULE = 1;

    final Messenger mMessenger = new Messenger(new ServiceHandler());

    /**
     * The main handler for the firewall service. The intent firewall will deliver messages to this
     * handler.
     */
    private final class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case CREATE_RULE: {
                    Bundle data = msg.getData();
                    Log.i(TAG, "Status for create rule: "+data.getInt("status"));
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Received bind request.");
        return mMessenger.getBinder();
    }

    public static Intent getCallingIntent(){
        Intent fwService = new Intent(GuardianApplication.getInstance(), IntentFirewallService.class);
        return fwService;
    }

}