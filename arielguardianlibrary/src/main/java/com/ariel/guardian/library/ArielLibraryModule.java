package com.ariel.guardian.library;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mikalackis on 23.8.16..
 */
@Module
public class ArielLibraryModule {

    private Application mApplication;

    public ArielLibraryModule(final Application application){
        mApplication = application;
    }

    @Provides
    @Singleton
    public Application providesApplication(){
        return mApplication;
    }

}
