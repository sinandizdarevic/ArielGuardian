package com.ariel.guardian.sync;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.pubnub.ArielPubNubCallback;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

/**
 * Created by mikalackis on 4.7.16..
 */
public class PubNubCallback extends ArielPubNubCallback {

    private final String TAG = "PubNubCallback";

    private Gson mGson = ArielUtilities.getGson();

    private Context mContext;

    private ArielDatabase mDatabase;
    private ArielPubNub mPubNub;

    public PubNubCallback(final Context context, final ArielDatabase database, final ArielPubNub pubnub) {
        super(context, database, pubnub);
        this.mContext = context;
        this.mDatabase = database;
        this.mPubNub = pubnub;
    }

    @Override
    protected void pubnubConnected() {
        Logger.d( "invoking pubnubConnected");
        // send any remaining messages
        mContext.startService(SyncIntentService.getSyncIntent(-1));
    }

}
