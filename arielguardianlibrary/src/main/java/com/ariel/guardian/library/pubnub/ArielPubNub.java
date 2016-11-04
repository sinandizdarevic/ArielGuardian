package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.Gson;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mikalackis on 14.10.16..
 */

public class ArielPubNub implements ArielPubNubInterface {

    public static final String TAG = "ArielPubNub";

    private String[] mPubNubChannels;

    private Context mContext;

    private Gson mGson;

    private PubNubManager mPubNubManager;

    ArielDatabase mArielDatabase;

    public ArielPubNub(final Context context, final ArielDatabase database){
        mContext = context;
        mArielDatabase = database;
        mGson = ArielUtilities.getGson();
        mPubNubManager = new PubNubManager(context);
        subscribeToChannelsFromDB();
    }

    @Override
    public long createApplicationMessage(DeviceApplication deviceApplication, String action, boolean reportReception) {
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE_APPLICATION);
        message.setDataObject(mGson.toJson(deviceApplication));
        message.setReportReception(reportReception);
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public long createLocationMessage(long locationId, String action, boolean reportReception) {
        DeviceLocation deviceLocation = mArielDatabase
                .getLocationByID(locationId);
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE_LOCATION);
        message.setDataObject(mGson.toJson(deviceLocation));
        message.setReportReception(reportReception);
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public long createConfigurationMessage(long configID, String action, boolean reportReception) {
        Configuration deviceConfiguration = mArielDatabase
                .getConfigurationByID(configID);
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE_CONFIGURATION);
        message.setDataObject(mGson.toJson(deviceConfiguration));
        message.setReportReception(reportReception);
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public long createWrapperMessage(WrapperMessage message) {
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public void sendMessage(Object commandMessage, PNCallback<PNPublishResult> callback) {
        mPubNubManager.sendMessage(commandMessage, callback);
    }

    @Override
    public void reconnect() {
        mPubNubManager.reconnect();
    }

    @Override
    public void subscribeToChannels(String... channels) {
        mPubNubChannels = channels;
        mPubNubManager.subscribeToChannels(channels);
    }

    @Override
    public void subscribeToChannelsFromDB() {
        List<ArielDevice> devices = mArielDatabase.getAllDevices();
        if(devices!=null && devices.size()>0) {
            mPubNubChannels = new String[devices.size()];
            Iterator<ArielDevice> devicesIt = devices.iterator();
            int i = 0;
            while(devicesIt.hasNext()){
                ArielDevice device = devicesIt.next();
                Log.i(ArielDatabase.TAG, "Adding channel: "+device.getArielChannel());
                mPubNubChannels[i] = device.getArielChannel();
                i++;
            }
        }

        if(mPubNubChannels!=null) {
            mPubNubManager.subscribeToChannels(mPubNubChannels);
        }
    }

    @Override
    public void addSubscribeCallback(SubscribeCallback callback) {
        mPubNubManager.addSubscribeCallback(callback);
    }

}
