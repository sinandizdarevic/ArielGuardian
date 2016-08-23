package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.utils.Utilities;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.endpoints.pubsub.Publish;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNWhereNowResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Main PubNub manager class. Managed by PubNubService
 */
public class PubNubManager {

    private final String TAG = "PubNubManager";

    private PubNub pubnub = null;
    private WeakReference<Context> refContext;

    private ArrayList<String> mSubscribedChannels;

    public PubNubManager() {
        mSubscribedChannels = new ArrayList<>();
    }

    /**
     * Main initialization point of PubNubManager. You have to call this every time
     * any of the parameters change
     * @param publishKey
     * @param subscribeKey
     * @param secretKey
     * @param cipherKey
     * @param callback
     */
    public void init(final String publishKey, final String subscribeKey, final String secretKey, final String cipherKey, final SubscribeCallback callback){

        if(pubnub!=null){
            cleanUp();
        }

        PNConfiguration pubNubConfig = new PNConfiguration();
        pubNubConfig.setPublishKey(publishKey);
        pubNubConfig.setSubscribeKey(subscribeKey);
        //pubNubConfig.setSecretKey(secretKey);
        pubNubConfig.setCipherKey(cipherKey);
        pubNubConfig.setSecure(true);
        pubNubConfig.setUuid(Utilities.getUniquePsuedoID());
        pubNubConfig.setLogVerbosity(PNLogVerbosity.BODY);

        //pubNubConfig.setPresenceTimeout()

        pubnub = new PubNub(pubNubConfig);

        subscribeToChannels(Utilities.getPubNubConfigChannel(),
                Utilities.getPubNubLocationChannel(), Utilities.getPubNubApplicationChannel());

        addListener(callback);
    }

    /**
     * Method that performs cleaning and destroys PubNub instance
     */
    public void cleanUp(){
        if(pubnub!=null) {
            pubnub.unsubscribe().channels(mSubscribedChannels);
        }
        mSubscribedChannels.clear();
        pubnub.stop();
    }

    public void listPubNubChannels(){
        pubnub.whereNow()
                .uuid(Utilities.getUniquePsuedoID())
                .async(new PNCallback<PNWhereNowResult>() {
                    @Override
                    public void onResponse(PNWhereNowResult result, PNStatus status) {
                        // returns a pojo with channels // channel groups which I am part of.
                        Iterator<String> it = result.getChannels().iterator();
                        while(it.hasNext()){
                            Log.i("CHANNELS", it.next());
                        }
                    }
                });
    }

    private void subscribeToChannels(String... channels){
        pubnub.subscribe().channels(Arrays.asList(channels)).execute();
        mSubscribedChannels.addAll(Arrays.asList(channels));
    }

    private void addListener(final SubscribeCallback listener){
        pubnub.addListener(listener);
    }

    /**
     * Method used to send a command to specific channel
     * @param command
     * @param channel
     */
    public void sendCommand(final CommandMessage command, final String channel, final PNCallback<PNPublishResult> callback){
        if(callback==null){
            throw new NullPointerException("Callback must not be null");
        }
        pubnub.publish().channel(channel).message(command).async(callback);
    }

}
