package com.ariel.guardian.library;

import android.content.Context;
import android.content.Intent;

import com.ariel.guardian.library.services.PubNubService;

/**
 * Created by mikalackis on 29.7.16..
 */
public final class ArielGuardianLibrary {

    public static void prepare(final Context context){

        // start service for pubnub and initialize
        Intent pubNubService = new Intent(context, PubNubService.class);
        context.startService(pubNubService);

        // fetch device configuration

    }


}