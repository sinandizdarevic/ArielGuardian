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

    public void savePubNubData(final DeviceConfiguration deviceConfiguration){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PUBNUB_SUBSCRIBE_KEY, deviceConfiguration.getPubNubSubscribeKey());
        editor.putString(PUBNUB_PUBLISH_KEY, deviceConfiguration.getPubNubPublishKey());
        editor.putString(PUBNUB_SECRET_KEY, deviceConfiguration.getPubNubSecretKey());
        editor.putString(PUBNUB_CIPHER_KEY, deviceConfiguration.getPubNubCipherKey());
        editor.putBoolean(PUBNUB_DATA_SET, true);
        editor.commit();
    }

    public void clearPubNubData(){
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PUBNUB_SUBSCRIBE_KEY, "");
        editor.putString(PUBNUB_PUBLISH_KEY, "");
        editor.putString(PUBNUB_SECRET_KEY, "");
        editor.putString(PUBNUB_CIPHER_KEY, "");
        editor.putBoolean(PUBNUB_DATA_SET, false);
        editor.commit();
    }

    public DeviceConfiguration getPubNubData(){
        boolean dataSet = mSharedPreferences.getBoolean(PUBNUB_DATA_SET, false);
        DeviceConfiguration deviceConfiguration = null;
        if(dataSet) {
            deviceConfiguration = new DeviceConfiguration();
            deviceConfiguration.setPubNubCipherKey(mSharedPreferences.getString(PUBNUB_CIPHER_KEY, ""));
            deviceConfiguration.setPubNubPublishKey(mSharedPreferences.getString(PUBNUB_PUBLISH_KEY, ""));
            deviceConfiguration.setPubNubSecretKey(mSharedPreferences.getString(PUBNUB_SECRET_KEY, ""));
            deviceConfiguration.setPubNubSubscribeKey(mSharedPreferences.getString(PUBNUB_SUBSCRIBE_KEY, ""));
            return deviceConfiguration;
        }
        return deviceConfiguration;
    }

}
