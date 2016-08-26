package com.ariel.guardian.library.pubnub;

import android.content.Context;
import android.util.Log;

import com.ariel.guardian.library.R;
import com.ariel.guardian.library.commands.CommandMessage;
import com.ariel.guardian.library.utils.SharedPrefsManager;
import com.ariel.guardian.library.utils.Utilities;
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
import java.util.HashSet;
import java.util.Iterator;

/**
 * Main PubNub manager class. Managed by PubNubService
 */
public class PubNubManager {

    private final String TAG = "PubNubManager";

    private PubNub pubnub = null;
    private WeakReference<Context> refContext;

    private ArrayList<String> mSubscribedChannels;

    private SubscribeCallback mSubscribeListener;

    public PubNubManager(final Context context) {
        mSubscribedChannels = new ArrayList<>();
        if(pubnub!=null){
            cleanUp();
        }

        refContext = new WeakReference<>(context);

        PNConfiguration pubNubConfig = new PNConfiguration();
        pubNubConfig.setPublishKey(context.getString(R.string.pubnub_publish_key));
        pubNubConfig.setSubscribeKey(context.getString(R.string.pubnub_subscribe_key));
        //pubNubConfig.setSecretKey(secretKey);
        pubNubConfig.setCipherKey(context.getString(R.string.pubnub_cipher_key));
        pubNubConfig.setSecure(true);
        pubNubConfig.setUuid(Utilities.getUniquePsuedoID());
        pubNubConfig.setLogVerbosity(PNLogVerbosity.BODY);

        //pubNubConfig.setPresenceTimeout()

        pubnub = new PubNub(pubNubConfig);

    }

    /**
     * Register SubscribeCallback
     * @param callback
     */
    public void addSubscribeCallback(final SubscribeCallback callback){
        if(mSubscribeListener!=null){
            pubnub.removeListener(mSubscribeListener);
        }
        mSubscribeListener = callback;
        pubnub.addListener(callback);
    }

    /**
     * Method that performs cleaning and destroys PubNub instance
     */
    public void cleanUp(){
        if(pubnub!=null) {
            pubnub.unsubscribe().channels(mSubscribedChannels);
            pubnub.removeListener(mSubscribeListener);
        }

//        if(refContext.get()!=null) {
//            SharedPrefsManager.getInstance(refContext.get()).
//                    setStringSetPreference(SharedPrefsManager.PUBNUB_CHANNELS,
//                            new HashSet<>(mSubscribedChannels));
//        }

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
        pubnub.unsubscribe().channels(mSubscribedChannels);
        mSubscribedChannels.clear();
        Log.i(TAG, "Subscribing to channels: "+channels.toString());
        pubnub.subscribe().channels(Arrays.asList(channels)).execute();
        mSubscribedChannels.addAll(Arrays.asList(channels));
    }

    /**
     * Method used to send a command to specific channel
     * @param command
     * @param channel
     */
    public void sendCommand(final CommandMessage command, final String channel, final PNCallback<PNPublishResult> callback){
        if(callback!=null){
            pubnub.publish().channel(channel).message(command).async(callback);
        }
        else{
            pubnub.publish().channel(channel).message(command);
        }
    }

}
