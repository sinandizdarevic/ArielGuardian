package com.ariel.guardian.library.pubnub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.library.db.RealmDatabaseManager;
import com.ariel.guardian.library.db.model.Configuration;
import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.DeviceLocation;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.ariel.guardian.library.services.SyncIntentService;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.Gson;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

import java.util.Calendar;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by mikalackis on 14.10.16..
 */

public class ArielPubNub implements ArielPubNubInterface {

    public static final String TAG = "ArielPubNub";

    private static ArielPubNub mInstance;

    private PubNubServiceInterface mPubNubService;

    private String mPubNubChannel;

    private Context mContext;

    private Gson mGson;

    public ArielPubNub(final Context context, final String pubNubChannel){
        mPubNubChannel = pubNubChannel;
        mContext = context;
        mGson = ArielUtilities.getGson();
        Intent pubNubServiceIntent = new Intent(context, PubNubService.class);
        pubNubServiceIntent.putExtra(PubNubService.EXTRA_PUBNUB_CHANNEL, mPubNubChannel);
        context.startService(pubNubServiceIntent);
        context.bindService(pubNubServiceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void sendApplicationMessage(String packageName, String action) {
        DeviceApplication deviceApp = RealmDatabaseManager.getInstance(mContext)
                .getApplicationByPackageName(packageName);
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setType(action);
        message.setDataObject(mGson.toJson(deviceApp));
        RealmDatabaseManager.getInstance(mContext).createWrapperMessage(message);
        mContext.startService(SyncIntentService.getSyncIntent(message.getId()));
    }

    @Override
    public void sendLocationMessage(long locationId, String action) {
        DeviceLocation deviceLocation = RealmDatabaseManager.getInstance(mContext)
                .getLocationByID(locationId);
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setType(action);
        message.setDataObject(mGson.toJson(deviceLocation));
        RealmDatabaseManager.getInstance(mContext).createWrapperMessage(message);
        mContext.startService(SyncIntentService.getSyncIntent(message.getId()));
    }

    @Override
    public void sendConfigurationMessage(long configID, String action) {
        Configuration deviceConfiguration = RealmDatabaseManager.getInstance(mContext)
                .getConfigurationByID(configID);
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setType(action);
        message.setDataObject(mGson.toJson(deviceConfiguration));
        RealmDatabaseManager.getInstance(mContext).createWrapperMessage(message);
        mContext.startService(SyncIntentService.getSyncIntent(message.getId()));
    }

    @Override
    public void sendMessage(Object commandMessage, PNCallback<PNPublishResult> callback) {
        mPubNubService.sendMessage(commandMessage, callback, mPubNubChannel);
    }

    @Override
    public void reconnect() {
        mPubNubService.reconnect();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service bound!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) service;
            mPubNubService = binder.getService();

            //mPubNubService.subscribeToChannelsWithCallback(new ArielPubNubCallback(), mPubNubChannel);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
