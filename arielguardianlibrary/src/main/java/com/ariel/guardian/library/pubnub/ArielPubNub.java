package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

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
import com.google.zxing.WriterException;
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
    public long createApplicationMessage(DeviceApplication deviceApplication, String action, boolean reportReception) {
        final WrapperMessage message = new WrapperMessage();
        message.setId(Calendar.getInstance().getTimeInMillis());
        message.setSender(ArielUtilities.getUniquePseudoID());
        message.setActionType(action);
        message.setMessageType(ArielConstants.MESSAGES.APPLICATION);
        message.setDataObject(mGson.toJson(deviceApplication));
        message.setReportReception(reportReception);
        message.setOriginalMessageType(null);
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
        message.setMessageType(ArielConstants.MESSAGES.LOCATION);
        message.setDataObject(mGson.toJson(deviceLocation));
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
        message.setMessageType(ArielConstants.MESSAGES.CONFIGURATION);
        message.setDataObject(mGson.toJson(deviceConfiguration));
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
            Log.i(TAG, "Already subscribed to channels");
        }
        else{
            Log.i(TAG, "Not subscribed to channels, do it now");
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
                Log.i(ArielDatabase.TAG, "Adding channel from missed messages: " + device.getArielChannel() + " with index: "+i);
                dbChannels[i] = device.getArielChannel();
                i++;
            }
        }

        return dbChannels;
    }

    @Override
    public void getMissedMessages() {
        long lastMessage = SharedPrefsManager.getInstance(mContext).getLongPreferences(SharedPrefsManager.KEY_LAST_PUBNUB_MESSAGE, -1);
        Log.i(TAG, "Last message time: "+lastMessage);
        Log.i(TAG, "Current time: "+System.currentTimeMillis());

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
                                        Log.i(TAG, "HIstory message time: "+item.getTimetoken());
                                        Log.i(TAG, "HIstory message: "+item.getEntry().toString());
                                    }
                                    subscribeToChannelsFromDB();
                                }
                                else{
                                    Log.i(TAG, "No old messages");
                                    subscribeToChannelsFromDB();
                                }
                            }
                        });
            }
        } else{
            Log.i(TAG, "No old messages");
            subscribeToChannelsFromDB();
        }
    }

    public void processPubNubMessage(final WrapperMessage currentMessage) {
        // its not a message from me, deal with it
        // check if the sender is actually an ArielDevice
        ArielDevice arielDevice = mArielDatabase.getDeviceByID(currentMessage.getSender());
        if (arielDevice != null) {
            Log.i(TAG, "This message came from an ArielDevice!!");
            messageForMasterDevice(currentMessage);
        } else {
            // Checking if the sender is the ArielMaster device.
            ArielMaster arielMaster = mArielDatabase.getMasterByID(currentMessage.getSender());
            if (arielMaster != null) {
                Log.i(TAG, "This message came from an ArielMaster!!");
                // this was a message from the ArielMaster device, deal with it
                // First, check if I am a master device
                ArielMaster amIMaster = mArielDatabase.getMasterByID(ArielUtilities.getUniquePseudoID());
                if (amIMaster != null) {
                    Log.i(TAG, "I'm also a master device");
                    // I'm also a master device and the message was from master device, so store the data
                    // and store the wrappermessage which will be deleted on report
                    messageForMasterDevice(currentMessage);
                    currentMessage.setSent(true);
                    mArielDatabase.createWrapperMessage(currentMessage);
                } else {
                    Log.i(TAG, "I'm an ArielDevice!");
                    messageForArielDevice(currentMessage);
                }

            } else {
                Log.i(TAG, "This is weird, unknown message???");
            }
        }
    }

    private void messageForMasterDevice(final WrapperMessage currentMessage) {
        String messageType = currentMessage.getMessageType();
        if(messageType!=null && messageType.length()>0){
            if(messageType.equals(ArielConstants.MESSAGES.REPORT)){
                // get the original message type
                String originalMessageType = currentMessage.getOriginalMessageType();
                // first, lets process the data and see what it contains
                if(originalMessageType.equals(ArielConstants.MESSAGES.APPLICATION)){
                    handleApplicationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGES.LOCATION)){
                    handleLocationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGES.CONFIGURATION)){
                    handleConfigurationMessage(currentMessage);
                }
                // we need to remove the wrapper message
                mArielDatabase.deleteWrapperMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.APPLICATION)){
                handleApplicationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.LOCATION)){
                handleLocationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.CONFIGURATION)){
                handleConfigurationMessage(currentMessage);
            }
        }
    }

    private void messageForArielDevice(final WrapperMessage currentMessage) {
        String messageType = currentMessage.getMessageType();
        if(messageType!=null && messageType.length()>0){
            if(messageType.equals(ArielConstants.MESSAGES.REPORT)){
                // get the original message type
                String originalMessageType = currentMessage.getOriginalMessageType();
                // first, lets process the data and see what it contains
                if(originalMessageType.equals(ArielConstants.MESSAGES.APPLICATION)){
                    handleApplicationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGES.LOCATION)){
                    handleLocationMessage(currentMessage);
                } else if(originalMessageType.equals(ArielConstants.MESSAGES.CONFIGURATION)){
                    handleConfigurationMessage(currentMessage);
                }
                // we need to remove the wrapper message
                mArielDatabase.deleteWrapperMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.APPLICATION)){
                handleApplicationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.LOCATION)){
                handleLocationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.CONFIGURATION)){
                handleConfigurationMessage(currentMessage);
            }

        }
        // send the report
        // first, set the original messageType
        currentMessage.setOriginalMessageType(currentMessage.getMessageType());
        // now mark this message as report
        currentMessage.setMessageType(ArielConstants.MESSAGES.REPORT);
        currentMessage.setSent(true);
        currentMessage.setSender(ArielUtilities.getUniquePseudoID());
        long id = createWrapperMessage(currentMessage);
        sendMessage(currentMessage);
    }

    private void handleReportMessage(WrapperMessage currentMessage){

    }

    private void handleLocationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.LOCATION_UPDATE)) {
            // update device location and broadcast change
            DeviceLocation deviceLocation = mGson.fromJson(currentMessage.getDataObject(), DeviceLocation.class);
            mArielDatabase.createLocation(deviceLocation);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTIONS.LOCATION_UPDATE);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceLocation.getId());
            mContext.sendBroadcast(appIntent);
        }
    }

    private void handleApplicationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_UPDATED)) {
            // add an app to the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            mArielDatabase.createOrUpdateApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_UPDATED);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
            mContext.sendBroadcast(appIntent);

        } else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_ADDED)) {
            // add an app to the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            mArielDatabase.createOrUpdateApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_ADDED);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
            mContext.sendBroadcast(appIntent);
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_REMOVED)) {
            // remove an app from the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            mArielDatabase.deleteApplication(deviceApp);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_REMOVED);
            mContext.sendBroadcast(appIntent);
        }
    }

    private void handleConfigurationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.DEVICE_CONFIG_UPDATE)) {
            // update device configuration and broadcast change
            Configuration deviceConfig = mGson.fromJson(currentMessage.getDataObject(), Configuration.class);
            mArielDatabase.createConfiguration(deviceConfig);
            Intent appIntent = new Intent();
            appIntent.setAction(ArielConstants.ACTIONS.DEVICE_CONFIG_UPDATE);
            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceConfig.getId());
            mContext.sendBroadcast(appIntent);
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.GET_DEVICE_QR_CODE)) {
            try {
                Bitmap qr_code = ArielUtilities.generateDeviceQRCode(ArielUtilities.getUniquePseudoID());
                String base64bitmap = ArielUtilities.base64Encode2String(qr_code);
//                    dataToTransfer = base64bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(final WrapperMessage message) {
        sendMessage(message, new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (!status.isError()) {
                    // everything is ok, remove wrapper message from realm
                    Log.i(ArielDatabase.TAG, "Message sent, remove wrapper");
                    // this part of code should be on parent side
//                    message.setSent(true);
//                    mArielDatabase.createWrapperMessage(message);

                    // since im an ArielDevice, i need to remove the message
                    mArielDatabase.deleteWrapperMessageByID(message.getId());
                } else {
                    // keep trying until you send the message
                    // this should probably be replaced with some advanced mechanism
                    Log.i(ArielDatabase.TAG, "Status is error: " + status.isError());
                    Log.i(ArielDatabase.TAG, "Status error details: " + status.getErrorData().getInformation());
                    Log.i(ArielDatabase.TAG, "Status error exception: " + status.getErrorData().getThrowable().getMessage());
                    Log.i(ArielDatabase.TAG, "Status code: " + status.getStatusCode());
                    Log.i(ArielDatabase.TAG, "Message not sent, retry??");
                    status.retry();
                }
            }
        });
    }
//
//            Object dataToTransfer = null;


//            if (currentMessage.getReportReception()) {
//                Log.i(ArielDatabase.TAG,"This message requires report reception");
//                currentMessage.setDataObject(mGson.toJson(dataToTransfer));
//                currentMessage.setReportReception(false);
//                currentMessage.setSender(ArielUtilities.getUniquePseudoID());
//                currentMessage.setActionType(ArielConstants.TYPE_WRAPPER_MESSAGE_REPORT);
//                long id = mPubNub.createWrapperMessage(currentMessage);
//                messageProcessed(id);
//            }
//            else{
//                Log.i(ArielDatabase.TAG,"This message does not require report reception");
//                mDatabase.deleteWrapperMessageByID(currentMessage.getId());
//            }

}
