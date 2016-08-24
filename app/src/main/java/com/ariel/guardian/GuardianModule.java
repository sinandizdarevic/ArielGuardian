package com.ariel.guardian;


import android.app.Application;

import com.ariel.guardian.library.firebase.FirebaseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mikalackis on 23.8.16..
 */
@Module
public class GuardianModule {

    private Application mApplication;

    public GuardianModule(final Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public FirebaseHelper providesFirebaseHelper() {
        FirebaseHelper fHelper = new FirebaseHelper();
        return fHelper;
    }

    @Provides
    @Singleton
    public ArielJobScheduler providesArielJobScheduler() {
        ArielJobScheduler pnManager = new ArielJobScheduler(mApplication);
        return pnManager;
    }

}