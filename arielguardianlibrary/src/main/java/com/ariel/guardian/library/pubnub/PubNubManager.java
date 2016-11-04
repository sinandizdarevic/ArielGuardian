package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.BuildConfig;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.utils.ArielUtilities;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNWhereNowResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Main PubNub manager class. Managed by InstanceKeeperService
 */
public final class PubNubManager {

    private final String TAG = "PubNubManager";

    private PubNub pubnub = null;
    private WeakReference<Context> refContext;

    private ArrayList<String> mSubscribedChannels;

    private SubscribeCallback mSubscribeListener;

    public PubNubManager(final Context context) {
        mSubscribedChannels = new ArrayList<>();
        if (pubnub != null) {
            cleanUp();
        }

        refContext = new WeakReference<>(context);

        PNConfiguration pubNubConfig = new PNConfiguration();
        pubNubConfig.setPublishKey(BuildConfig.PN_PUBLISH_KEY);
        pubNubConfig.setSubscribeKey(BuildConfig.PN_SUBSCRIBE_KEY);
        //pubNubConfig.setSecretKey(secretKey);
        /**
         * The cipher key should be autogenerated on first boot of the device
         * and part of system secure settings
         */
        pubNubConfig.setCipherKey(BuildConfig.PN_CIPHER_KEY);
        pubNubConfig.setSecure(true);
        pubNubConfig.setUuid(ArielUtilities.getUniquePseudoID());
        pubNubConfig.setLogVerbosity(PNLogVerbosity.BODY);
        //pubNubConfig.setConnectTimeout()
        //pubNubConfig.setPresenceTimeout()

        pubnub = new PubNub(pubNubConfig);

//        mSubscribedChannels = new ArrayList<>(SharedPrefsManager.getInstance(context)
//                .getStringSetPreference(SharedPrefsManager.PUBNUB_CHANNELS));
//
//        if(mSubscribedChannels!=null && mSubscribedChannels.size()>0){
//            restoreChannelSubscription();
//        }

    }

    /**
     * Register SubscribeCallback
     *
     * @param callback
     */
    public void addSubscribeCallback(final SubscribeCallback callback) {
        if (mSubscribeListener != null) {
            pubnub.removeListener(mSubscribeListener);
        }
        mSubscribeListener = callback;
        pubnub.addListener(callback);
    }

    /**
     * Method that performs cleaning and destroys PubNub instance
     */
    public void cleanUp() {
        if (pubnub != null) {
//            if(mSubscribedChannels!=null && mSubscribedChannels.size()>0){
//                // backup channels
//                backupSubscribedChannels();
//            }
            pubnub.unsubscribe().channels(mSubscribedChannels);
            pubnub.removeListener(mSubscribeListener);
        }

        mSubscribedChannels.clear();
        pubnub.stop();
    }

    public void listPubNubChannels() {
        pubnub.whereNow()
                .uuid(ArielUtilities.getUniquePseudoID())
                .async(new PNCallback<PNWhereNowResult>() {
                    @Override
                    public void onResponse(PNWhereNowResult result, PNStatus status) {
                        // returns a pojo with channels // channel groups which I am part of.
                        Iterator<String> it = result.getChannels().iterator();
                        while (it.hasNext()) {
                            Log.i(TAG, "CHANNELS: " + it.next());
                        }
                    }
                });
    }

    public void subscribeToChannels(String... channels) {
        Log.i(TAG, "Subscribing to channels: " + channels.toString());
        List<String> currentChannels = pubnub.getSubscribedChannels();
        for (String channel : channels) {
            if(currentChannels.contains(channel)){
                // dont subscribe to same channel again
                return;
            }
        }
        pubnub.subscribe().channels(Arrays.asList(channels)).withPresence().execute();
        mSubscribedChannels.addAll(Arrays.asList(channels));
    }

    public void reconnect() {
        pubnub.reconnect();
    }

    public void restoreChannelSubscription() {
        pubnub.subscribe().channels(mSubscribedChannels).execute();
    }

    /**
     * Method used to send a command to specific channel
     *
     * @param command
     * @param channel
     */
    public void sendCommand(final CommandMessage command, final PNCallback<PNPublishResult> callback, final String... channels) {
        if (channels.length > 0) {
            for (String channel : channels
                    ) {
                if (callback != null) {
                    pubnub.publish().channel(channel).message(command).async(callback);
                } else {
                    pubnub.publish().channel(channel).message(command);
                }
            }
        }
    }

    /**
     * Method used to send a a message to specific channel
     *
     * @param command
     * @param channel
     */
    public void sendMessage(final Object message, final PNCallback<PNPublishResult> callback) {
        if (mSubscribedChannels != null && mSubscribedChannels.size() > 0) {
            String[] channels = mSubscribedChannels.toArray(new String[mSubscribedChannels.size()]);
            if (channels != null && channels.length > 0) {
                Log.i(ArielDatabase.TAG, "Sending message: " + message);
                for (String channel : channels
                        ) {
                    Log.i(ArielDatabase.TAG, "To channel: " + channel);
                    if (callback != null) {
                        pubnub.publish().channel(channel).message(message).async(callback);
                    } else {
                        pubnub.publish().channel(channel).message(message);
                    }
                }
            }
        }
    }

}
