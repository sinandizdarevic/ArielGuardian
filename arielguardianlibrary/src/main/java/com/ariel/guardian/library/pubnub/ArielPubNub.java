package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.content.Intent;

import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielDevice;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.DeviceLocation;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.ariel.guardian.library.utils.SharedPrefsManager;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;
import com.pubnub.api.models.consumer.history.PNHistoryResult;

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

    public ArielPubNub(final Context context, final ArielDatabase database) {
        mContext = context;
        mArielDatabase = database;
        mGson = ArielUtilities.getGson();
        mPubNubManager = new PubNubManager(context);
        // call history first then everything else
    }

    @Override
    public long createApplicationMessage(DeviceApplication deviceApplication, String action, boolean reportReception, boolean executed) {
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE.APPLICATION);
        message.setDataObject(mGson.toJson(deviceApplication));
        message.setReportReception(reportReception);
        if(reportReception){
            // if this action requires report, save the message id
            // in the object so that UI can now about it
            deviceApplication.setPendingActionId(message.getId());
            mArielDatabase.createOrUpdateApplication(deviceApplication);
        }
        message.setOriginalMessageType(null);
        message.setExecuted(executed);
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public long createLocationMessage(long locationId, String action, boolean reportReception) {
        String dataToSend = null;
        if(locationId!=-1){
            DeviceLocation deviceLocation = mArielDatabase
                    .getLocationByID(locationId);
            dataToSend = mGson.toJson(deviceLocation);
        }
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE.LOCATION);
        message.setDataObject(dataToSend);
        message.setReportReception(reportReception);
        message.setOriginalMessageType(null);
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
        message.setMessageType(ArielConstants.MESSAGE_TYPE.CONFIGURATION);
        message.setDataObject(mGson.toJson(deviceConfiguration));
        message.setReportReception(reportReception);
        message.setOriginalMessageType(null);
        mArielDatabase.createWrapperMessage(message);
        return message.getId();
    }

    @Override
    public long createQRCodeMessage(String qrCode, String action, boolean reportReception) {
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGE_TYPE.CONFIGURATION);
        message.setDataObject(qrCode);
        message.setReportReception(reportReception);
        message.setOriginalMessageType(null);
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
        if(mPubNubChannels!=null && mPubNubChannels.length>0){
            Logger.d("Already subscribed to channels");
        }
        else{
            Logger.d("Not subscribed to channels, do it now");
            mPubNubChannels = getChannelsFromDatabase();

            if (mPubNubChannels != null) {
                mPubNubManager.subscribeToChannels(mPubNubChannels);
            }
        }
    }

    @Override
    public void addSubscribeCallback(SubscribeCallback callback) {
        mPubNubManager.addSubscribeCallback(callback);
    }

    public WrapperMessage parseIncomingMessage(String message) {
        WrapperMessage msg = mGson.fromJson(message, WrapperMessage.class);
        if (msg != null) {
            if (msg.getSender().equals(ArielUtilities.getUniquePseudoID())) {
                //this is my message
                return null;
            } else {
                // this one is OK, pass it on
                return msg;
            }
        }
        return msg;
    }

    private String[] getChannelsFromDatabase(){
        List<ArielDevice> devices = mArielDatabase.getAllDevices();
        String[] dbChannels = null;
        if (devices != null && devices.size() > 0) {
            dbChannels = new String[devices.size()];
            Iterator<ArielDevice> devicesIt = devices.iterator();
            int i = 0;
            while (devicesIt.hasNext()) {
                ArielDevice device = devicesIt.next();
                Logger.d("Adding channel from missed messages: " + device.getArielChannel() + " with index: "+i);
                dbChannels[i] = device.getArielChannel();
                i++;
            }
        }

        return dbChannels;
    }

    @Override
    public void getMissedMessages() {
        long lastMessage = SharedPrefsManager.getInstance(mContext).getLongPreferences(SharedPrefsManager.KEY_LAST_PUBNUB_MESSAGE, -1);
        Logger.d( "Last message time: "+lastMessage);
        Logger.d( "Current time: "+System.currentTimeMillis());

        String[] channels = getChannelsFromDatabase();

        if(lastMessage!=-1 && channels!=null && channels.length>0){
            for(String channel : channels){
                mPubNubManager.getPubnub().history().channel(channel)
                        .start(lastMessage)
                        .reverse(true)
                        .includeTimetoken(true)
                        .async(new PNCallback<PNHistoryResult>() {
                            @Override
                            public void onResponse(PNHistoryResult result, PNStatus status) {
                                if(result!=null && result.getMessages()!=null && result.getMessages().size()>0){
                                    Iterator<PNHistoryItemResult> itResults = result.getMessages().iterator();
                                    while(itResults.hasNext()){
                                        PNHistoryItemResult item = itResults.next();
                                        WrapperMessage message = parseIncomingMessage(item.getEntry().toString());
                                        if(message!=null){
                                            processPubNubMessage(message);
                                        }
                                        Logger.d( "HIstory message time: "+item.getTimetoken());
                                        Logger.d( "HIstory message: "+item.getEntry().toString());
                                    }
                                    subscribeToChannelsFromDB();
                                }
                                else{
                                    Logger.d( "No old messages");
                                    subscribeToChannelsFromDB();
                                }
                            }
                        });
            }
        } else{
            Logger.d( "No old messages");
            subscribeToChannelsFromDB();
        }
    }

    public boolean processPubNubMessage(final WrapperMessage currentMessage) {

        boolean processed = false;

        // its not a message from me, deal with it
        // check if the sender is actually an ArielDevice
        ArielDevice arielDevice = mArielDatabase.getDeviceByID(currentMessage.getSender());
        if (arielDevice != null) {
            Logger.d( "This message came from an ArielDevice!!");
            messageForMasterDevice(currentMessage);
            processed = true;
        } else {
            // Checking if the sender is the ArielMaster device.
            ArielMaster arielMaster = mArielDatabase.getMasterByID(currentMessage.getSender());
            if (arielMaster != null) {
                Logger.d( "This message came from an ArielMaster!!");
                // this was a message from the ArielMaster device, deal with it
                // First, check if I am a master device
                ArielMaster amIMaster = mArielDatabase.getMasterByID(ArielUtilities.getUniquePseudoID());
                if (amIMaster != null) {
                    Logger.d( "I'm also a master device");
                    // I'm also a master device and the message was from master device, so store the data
                    // and store the wrappermessage which will be deleted on report
                    messageForMasterDevice(currentMessage);
                    currentMessage.setSent(true);
                    mArielDatabase.createWrapperMessage(currentMessage);
                    processed = true;
                } else {
                    Logger.d( "I'm an ArielDevice!");
                    //messageForArielDevice(currentMessage);
                    // do nothing, pass the message to the ArielDevice Guardian application
                    processed = false;
                }
            } else {
                Logger.d( "This is weird, unknown message???");
            }
        }

        return processed;
    }

    private void messageForMasterDevice(final WrapperMessage currentMessage) {
        String messageType = currentMessage.getMessageType();
        if(messageType!=null && messageType.length()>0){
            if(messageType.equals(ArielConstants.MESSAGE_TYPE.REPORT)){
                // we need to remove the wrapper message
                mArielDatabase.deleteWrapperMessage(currentMessage);
                // get the original message type
                String originalMessageType = currentMessage.getOriginalMessageType();
                // first, lets process the data and see what it contains
                if(originalMessageType.equals(ArielConstants.MESSAGE_TYPE.APPLICATION)){
                    handleApplicationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGE_TYPE.LOCATION)){
                    handleLocationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGE_TYPE.CONFIGURATION)){
                    handleConfigurationMessage(currentMessage);
                }
            } else if(messageType.equals(ArielConstants.MESSAGE_TYPE.APPLICATION)){
                handleApplicationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGE_TYPE.LOCATION)){
                handleLocationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGE_TYPE.CONFIGURATION)){
                handleConfigurationMessage(currentMessage);
            }
        }
    }

    private void handleLocationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTION.LOCATION_UPDATE)) {
            // update device location and broadcast change
            DeviceLocation deviceLocation = mGson.fromJson(currentMessage.getDataObject(), DeviceLocation.class);
            mArielDatabase.createLocation(deviceLocation);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTION.LOCATION_UPDATE);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceLocation.getId());
            mContext.sendBroadcast(appIntent);
        }
    }

    private void handleApplicationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTION.APPLICATION_UPDATED)) {
            // add an app to the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            // remove its pending action id
            deviceApp.setPendingActionId(0);
            mArielDatabase.createOrUpdateApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTION.APPLICATION_UPDATED);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
            mContext.sendBroadcast(appIntent);

        } else if (currentMessage.getActionType().equals(ArielConstants.ACTION.APPLICATION_ADDED)) {
            // add an app to the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            // remove its pending action id
            deviceApp.setPendingActionId(0);
            mArielDatabase.createOrUpdateApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTION.APPLICATION_ADDED);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
            mContext.sendBroadcast(appIntent);
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTION.APPLICATION_REMOVED)) {
            // remove an app from the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            // remove its pending action id
            deviceApp.setPendingActionId(0);
            mArielDatabase.deleteApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTION.APPLICATION_REMOVED);
            mContext.sendBroadcast(appIntent);
        }
    }

    private void handleConfigurationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTION.DEVICE_CONFIG_UPDATE)) {
            // update device configuration and broadcast change
            Configuration deviceConfig = mGson.fromJson(currentMessage.getDataObject(), Configuration.class);
            mArielDatabase.createConfiguration(deviceConfig);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTION.DEVICE_CONFIG_UPDATE);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceConfig.getId());
            mContext.sendBroadcast(appIntent);
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTION.GET_DEVICE_QR_CODE)) {
            Logger.i("Recived qrcode: "+currentMessage.getDataObject());
        }
    }

}
