package com.ariel.guardian.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ariel.guardian.library.firebase.model.DeviceConfiguration;

import java.util.Set;

/**
 * Created by mikalackis on 17.8.16..
 */
public class SharedPrefsManager {

    public static final String SHARED_PREFS = "ariel_library_preferences";

    public static final String PUBNUB_SUBSCRIBE_KEY = "pubnub_subscribe_key";
    public static final String PUBNUB_PUBLISH_KEY = "pubnub_publish_key";
    public static final String PUBNUB_SECRET_KEY = "pubnub_publish_key";
    public static final String PUBNUB_CIPHER_KEY = "pubnub_publish_key";
    public static final String PUBNUB_DATA_SET = "pubnub_data_set";

    public static final String PUBNUB_CHANNELS = "pubnub_channels";


    private static SharedPrefsManager sInstance;

    private SharedPreferences mSharedPreferences;

    private SharedPrefsManager(final Context context){
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFS, 0);
    }

    public static SharedPrefsManager getInstance(final Context context){
        if(sInstance==null){
            sInstance = new SharedPrefsManager(context);
        }
        return sInstance;
    }

    public String getStringPreference(final String key, final String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public Set<String> getStringSetPreference(final String key) {
        return mSharedPreferences.getStringSet(key, null);
    }

    public void setStringSetPreference(final String key, final Set<String> value) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void setStringPreference(final String key, final String value) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
