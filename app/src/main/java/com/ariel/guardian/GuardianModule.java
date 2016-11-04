package com.ariel.guardian;


import com.ariel.guardian.library.database.ArielDatabase;
import com.ariel.guardian.library.pubnub.ArielPubNub;
import com.ariel.guardian.sync.PubNubCallback;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mikalackis on 23.8.16..
 */
@Module
public class GuardianModule {

    private GuardianApplication mApplication;

    public GuardianModule(final GuardianApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public GuardianApplication providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public ArielJobScheduler providesArielJobScheduler() {
        ArielJobScheduler pnManager = new ArielJobScheduler(mApplication);
        return pnManager;
    }

    @Provides
    @Singleton
    public ArielDatabase providesArielDatabase() {
        ArielDatabase arielDatabase = new ArielDatabase(mApplication);
        return arielDatabase;
    }

    @Provides
    @Singleton
    public ArielPubNub providesArielPubNub() {
        ArielPubNub arielPubNub = new ArielPubNub(mApplication, providesArielDatabase());
        PubNubCallback callback = new PubNubCallback(mApplication, providesArielDatabase(), arielPubNub);
        arielPubNub.addSubscribeCallback(callback);
        return arielPubNub;
    }

}