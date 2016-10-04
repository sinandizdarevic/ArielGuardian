package com.ariel.guardian.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.pubnub.PubNubUtilities;

import javax.inject.Inject;

/**
 * Created by mikalackis on 5.9.16..
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    @Inject
    GuardianApplication mApplication;

    @Override
    public void onReceive(Context context, Intent intent) {

        GuardianApplication.getInstance().getGuardianComponent().inject(this);

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            mApplication.getPubNub().reconnect();
            PubNubUtilities.getInstance().unregisterReceiver();
        }
    }

}
