package com.ariel.guardian.pubnub;

import android.content.IntentFilter;

import com.ariel.guardian.GuardianApplication;
import com.ariel.guardian.receivers.NetworkChangeReceiver;

/**
 * Created by mikalackis on 5.9.16..
 */
public class PubNubUtilities {

    private NetworkChangeReceiver mReceiver;

    private static PubNubUtilities mInstance;

    private PubNubUtilities(){
        mReceiver = new NetworkChangeReceiver();
    }

    public static synchronized PubNubUtilities getInstance(){
        if(mInstance==null){
            mInstance = new PubNubUtilities();
        }
        return mInstance;
    }

    public void registerNetworkListener(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        GuardianApplication.getInstance().registerReceiver(mReceiver, filter);
    }

    public void unregisterReceiver(){
        GuardianApplication.getInstance().unregisterReceiver(mReceiver);
    }
}
