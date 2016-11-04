package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.utils.NetworkUtils;

import javax.inject.Inject;

/**
 * Created by mikalackis on 5.9.16..
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    @Inject
    ArielPubNub mArielPubNub;

    @Override
    public void onReceive(Context context, Intent intent) {
        GuardianApplication.getInstance().getGuardianComponent().inject(this);
        boolean isConnected = NetworkUtils.getConnectivityStatusString(context);
        if(isConnected){
            mArielPubNub.reconnect();
        }
    }

}
