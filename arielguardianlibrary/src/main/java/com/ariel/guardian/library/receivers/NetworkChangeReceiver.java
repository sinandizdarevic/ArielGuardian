package com.ariel.guardian.library.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ariel.guardian.library.ArielLibrary;
import com.ariel.guardian.library.pubnub.PubNubUtilities;

/**
 * Created by mikalackis on 5.9.16..
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            ArielLibrary.action().reconnect();
            PubNubUtilities.getInstance().unregisterReceiver();
        }
    }

}
