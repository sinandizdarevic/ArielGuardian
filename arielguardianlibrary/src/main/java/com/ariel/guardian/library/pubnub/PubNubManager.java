package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.utils.Utilities;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNLogVerbosity;
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

    public void init(final String publishKey, final String subscribeKey, final String secretKey, final String cipherKey){

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

        pubnub = new PubNub(pubNubConfig);
    }

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

    public void subscribeToChannels(String... channels){
        pubnub.subscribe().channels(Arrays.asList(channels)).execute();
        mSubscribedChannels.addAll(Arrays.asList(channels));
    }

    public void addListener(final SubscribeCallback listener){
        pubnub.addListener(listener);
    }

    public void sendCommand(final CommandMessage command, final String channel){
        pubnub.publish().channel(channel).message(command);
    }

}
