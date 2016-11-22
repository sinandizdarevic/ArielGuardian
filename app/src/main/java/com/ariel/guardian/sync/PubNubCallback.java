package com.ariel.guardian.sync;

import android.content.Context;

import com.ariel.guardian.command.ApplicationCommand;
import com.ariel.guardian.command.ConfigurationCommand;
import com.ariel.guardian.command.LocateNowCommand;
import com.ariel.guardian.command.MasterCommand;
import com.ariel.guardian.command.QRCodeCommand;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.database.model.WrapperMessage;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.library.pubnub.ArielPubNubCallback;
import com.ariel.guardian.library.utils.ArielConstants;
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
        //currentMessage.setSent(false);
        //currentMessage.setExecuted(false);

        // store the message in our own database
        //mArielDatabase.createWrapperMessage(currentMessage);

        // process it now
        String messageType = currentMessage.getMessageType();
        if(messageType!=null && messageType.length()>0){
            // can this even happen on the ArielDevice?
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
            LocateNowCommand locateNow = new LocateNowCommand.LocateNowBuilder()
                    .withMessage(currentMessage)
                    .build();
            locateNow.execute();
        }
    }

    private void handleApplicationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTION.APPLICATION_UPDATED)) {
            ApplicationCommand applicationCommand = new ApplicationCommand.ApplicationBuilder()
                    .withMessage(currentMessage)
                    .build();
            applicationCommand.execute();
        }
    }

    private void handleConfigurationMessage(WrapperMessage currentMessage){
        if (currentMessage.getActionType().equals(ArielConstants.ACTION.DEVICE_CONFIG_UPDATE)) {
            // update device configuration and broadcast change
            ConfigurationCommand configurationCommand = new ConfigurationCommand.ConfigurationBuilder()
                    .withMessage(currentMessage)
                    .build();
            configurationCommand.execute();
        } else if (currentMessage.getActionType().equals(ArielConstants.ACTION.GET_DEVICE_QR_CODE)) {
            QRCodeCommand configurationCommand = new QRCodeCommand.QRCodeBuilder()
                    .withMessage(currentMessage)
                    .build();
            configurationCommand.execute();
        } else if(currentMessage.getActionType().equals(ArielConstants.ACTION.ADD_MASTER_DEVICE)){
            MasterCommand masterCommand = new MasterCommand.MasterBuilder()
                    .withMessage(currentMessage)
                    .build();
            masterCommand.execute();
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
        currentMessage.setMessageType(ArielConstants.MESSAGE_TYPE.REPORT);
        currentMessage.setSender(ArielUtilities.getUniquePseudoID());
    }

}
