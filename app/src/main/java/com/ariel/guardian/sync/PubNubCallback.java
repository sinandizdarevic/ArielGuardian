package com.ariel.guardian.sync;

import android.content.Context;
import android.graphics.Bitmap;

import com.ariel.guardian.command.ApplicationCommand;
import com.ariel.guardian.command.ConfigurationCommand;
import com.ariel.guardian.command.LocateNowCommand;
import com.ariel.guardian.command.params.ApplicationParams;
import com.ariel.guardian.command.params.ConfigurationParams;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.ArielMaster;
import com.ariel.guardian.library.database.model.Configuration;
import com.ariel.guardian.library.database.model.DeviceApplication;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.pubnub.ArielPubNubCallback;
import com.ariel.guardian.library.utils.ArielConstants;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.orhanobut.logger.Logger;

import static android.R.id.message;

/**
 * Created by mikalackis on 4.7.16..
 */
public class PubNubCallback extends ArielPubNubCallback {

    private final String TAG = "PubNubCallback";

    private Gson mGson = ArielUtilities.getGson();

    private Context mContext;

    private ArielDatabase mArielDatabase;
    private ArielPubNub mPubNub;

    public PubNubCallback(final Context context, final ArielDatabase database, final ArielPubNub pubnub) {
        super(context, database, pubnub);
        this.mContext = context;
        this.mArielDatabase = database;
        this.mPubNub = pubnub;
    }

    @Override
    protected void pubnubConnected() {
        Logger.d( "invoking pubnubConnected");
        // send any remaining messages
        mContext.startService(SyncIntentService.getSyncIntent(-1));
    }

    @Override
    protected void handleMessage(WrapperMessage currentMessage) {
        // make sure the message doesnt get sent before we actually execute
        // the action it contains
        currentMessage.setSent(false);
        currentMessage.setExecuted(false);

        // store the message in our own database
        mArielDatabase.createWrapperMessage(currentMessage);

        // process it now
        String messageType = currentMessage.getMessageType();
        if(messageType!=null && messageType.length()>0){
            // can this even happen on the ArielDevice?
            if(messageType.equals(ArielConstants.MESSAGES.REPORT)){
                // we need to remove the wrapper message
                mArielDatabase.deleteWrapperMessage(currentMessage);
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

            } else if(messageType.equals(ArielConstants.MESSAGES.APPLICATION)){
                handleApplicationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.LOCATION)){
                handleLocationMessage(currentMessage);
            } else if(messageType.equals(ArielConstants.MESSAGES.CONFIGURATION)){
                handleConfigurationMessage(currentMessage);
            }

        }
    }

    private void handleLocationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.LOCATION_UPDATE)) {
            // update device location and broadcast change
//            DeviceLocation deviceLocation = mGson.fromJson(currentMessage.getDataObject(), DeviceLocation.class);
//            mArielDatabase.createLocation(deviceLocation);
//            Intent appIntent = new Intent();
//            appIntent.setAction(ArielConstants.ACTIONS.LOCATION_UPDATE);
//            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceLocation.getId());
//            mContext.sendBroadcast(appIntent);

            LocateNowCommand locateNow = new LocateNowCommand.LocateNowBuilder()
                    .withMessage(currentMessage)
                    .withParams(null)
                    .build();
            locateNow.execute();
        }
    }

    private void handleApplicationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_UPDATED)) {
            // add an app to the realm database
            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
            mArielDatabase.createOrUpdateApplication(deviceApp);
//            Intent appIntent = new Intent();
//            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_UPDATED);
//            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
//            mContext.sendBroadcast(appIntent);
            ApplicationCommand applicationCommand = new ApplicationCommand.ApplicationBuilder()
                    .withMessage(currentMessage)
                    .withParams(new ApplicationParams.ApplicationParamBuilder(deviceApp.getPackageName()).build())
                    .build();
            applicationCommand.execute();
        }


//        else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_ADDED)) {
//            // add an app to the realm database
//            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
//            mArielDatabase.createOrUpdateApplication(deviceApp);
//            Intent appIntent = new Intent();
//            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_ADDED);
//            appIntent.putExtra(ArielConstants.EXTRA_DATABASE_ID, deviceApp.getPackageName());
//            mContext.sendBroadcast(appIntent);
//        } else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.APPLICATION_REMOVED)) {
//            // remove an app from the realm database
//            DeviceApplication deviceApp = mGson.fromJson(currentMessage.getDataObject(), DeviceApplication.class);
//            mArielDatabase.deleteApplication(deviceApp);
//            Intent appIntent = new Intent();
//            appIntent.setAction(ArielConstants.ACTIONS.APPLICATION_REMOVED);
//            mContext.sendBroadcast(appIntent);
//        }
    }

    private void handleConfigurationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.DEVICE_CONFIG_UPDATE)) {
            // update device configuration and broadcast change
            Configuration deviceConfig = mGson.fromJson(currentMessage.getDataObject(), Configuration.class);
            mArielDatabase.createConfiguration(deviceConfig);

            ConfigurationCommand configurationCommand = new ConfigurationCommand.ConfigurationBuilder()
                    .withMessage(currentMessage)
                    .withParams(new ConfigurationParams.ConfigParamBuilder().configurationId(deviceConfig.getId()).build())
                    .build();
            configurationCommand.execute();
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTIONS.GET_DEVICE_QR_CODE)) {
            try {
                Bitmap qr_code = ArielUtilities.generateDeviceQRCode(ArielUtilities.getUniquePseudoID());
                String base64bitmap = ArielUtilities.base64Encode2String(qr_code);

                long id = mPubNub.createQRCodeMessage(base64bitmap, ArielConstants.ACTIONS.GET_DEVICE_QR_CODE, true);
                mContext.startService(SyncIntentService.getSyncIntent(id));
//                    dataToTransfer = base64bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else if(currentMessage.getActionType().equals(ArielConstants.ACTIONS.ADD_MASTER_DEVICE)){
            ArielMaster arielMaster = mGson.fromJson(currentMessage.getDataObject(), ArielMaster.class);
            mArielDatabase.createOrUpdateMaster(arielMaster);

            currentMessage.setOriginalMessageType(currentMessage.getMessageType());
            currentMessage.setMessageType(ArielConstants.MESSAGES.REPORT);
            currentMessage.setSender(ArielUtilities.getUniquePseudoID());
            currentMessage.setExecuted(true);
            mArielDatabase.createWrapperMessage(currentMessage);
            mContext.startService(SyncIntentService.getSyncIntent(currentMessage.getId()));
        }
    }

    private void messageForArielDevice(final WrapperMessage currentMessage) {
        /**
         * ArielDevice message should be handled in a different process. Steps:
         * 1. Save the incoming message as a new message with type REPORT
         * 2. Set sent=false and executed=false
         * 3. Broadcast the appropriate action and message id
         * 4. Catch the broadcast and pass the ID of the message to any component
         * that performs the action
         * 5. Once the action is completed, fetch the message, set executed=true and save
         * 6. Call SyncIntentService with that ID
         */

        currentMessage.setOriginalMessageType(currentMessage.getMessageType());
        currentMessage.setMessageType(ArielConstants.MESSAGES.REPORT);
        currentMessage.setSender(ArielUtilities.getUniquePseudoID());
    }

}
