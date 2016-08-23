package com.ariel.guardian.library;

import android.content.Context;
import android.content.Intent;

import com.ariel.guardian.library.services.PubNubService;

/**
 * Ariel library main entry point.
 */
public class ArielLibrary {

    /**
     * Performs library initialization
     * @param context
     */
    public static void prepare(final Context context){
        // starts pubnub service
        Intent pubNubService = new Intent(context, PubNubService.class);
        context.startService(pubNubService);
    }

}
