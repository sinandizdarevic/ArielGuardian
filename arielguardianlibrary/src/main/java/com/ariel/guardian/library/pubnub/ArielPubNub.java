package com.ariel.guardian.library.pubnub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ariel.guardian.library.Ariel;
import com.ariel.guardian.library.db.RealmDatabaseManager;
import com.ariel.guardian.library.db.model.ArielDevice;
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
import java.util.Iterator;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by mikalackis on 14.10.16..
 */

public class ArielPubNub implements ArielPubNubInterface {

    public static final String TAG = "ArielPubNub";

    private static ArielPubNub mInstance;

    private PubNubServiceInterface mPubNubService;

    private String[] mPubNubChannels;

    private Context mContext;

    private Gson mGson;

    public ArielPubNub(final Context context){
        mContext = context;
        mGson = ArielUtilities.getGson();

        List<ArielDevice> devices = RealmDatabaseManager.getInstance(context).getAllDevices();
        if(devices!=null && devices.size()>0) {
            mPubNubChannels = new String[devices.size()];
            Iterator<ArielDevice> devicesIt = devices.iterator();
            int i = 0;
            while(devicesIt.hasNext()){
                ArielDevice device = devicesIt.next();
                mPubNubChannels[i] = device.getArielChannel();
                i++;
            }
        }

        Intent pubNubServiceIntent = new Intent(context, PubNubService.class);
        context.startService(pubNubServiceIntent);
        context.bindService(pubNubServiceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void sendApplicationMessage(String packageName, String action) {
        DeviceApplication deviceApp = RealmDatabaseManager.getInstance(mContext)
                .getApplicationByID(packageName);
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
        mPubNubService.sendMessage(commandMessage, callback, mPubNubChannels);
    }

    @Override
    public void reconnect() {
        mPubNubService.reconnect();
    }

    @Override
    public void subscribeToChannels(String... channels) {
        mPubNubService.subscribeToChannels(channels);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Service bound!");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PubNubService.PubNubServiceBinder binder = (PubNubService.PubNubServiceBinder) service;
            mPubNubService = binder.getService();

            if(mPubNubChannels!=null && mPubNubChannels.length>0){
                mPubNubService.subscribeToChannels(mPubNubChannels);
            }

            //mPubNubService.subscribeToChannelsWithCallback(new ArielPubNubCallback(), mPubNubChannel);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
