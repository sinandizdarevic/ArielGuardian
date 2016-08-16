package com.ariel.guardian.library.firebase;

import android.util.ArrayMap;
import android.util.Log;

import com.ariel.guardian.library.model.DeviceActivity;
import com.ariel.guardian.library.model.DeviceLocation;
import com.ariel.guardian.library.model.DevicePackage;
import com.ariel.guardian.library.utils.Utilities;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by mikalackis on 23.5.16..
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    // firebase config keys
    public static final String UPDATE_CONFIG_COMMAND_KEY = "update_config_command";
    public static final String LOCATE_COMMAND_KEY = "locate_command";
    public static final String TRACK_START_COMMAND_KEY = "track_start_command";
    public static final String TRACK_STOP_COMMAND_KEY = "track_stop_command";

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    private static FirebaseHelper mInstance;
    private FirebaseDatabase mFBDB;

    private ArrayMap<String, ValueEventListener> mPackageListeners = new ArrayMap<String, ValueEventListener>();

    public static FirebaseHelper getInstance(){
        if (mInstance==null){
            mInstance = new FirebaseHelper();
        }
        return mInstance;
    }

    private FirebaseHelper(){
        Log.i(TAG, "FirebaseHelper instantiated");
        mFBDB = FirebaseDatabase.getInstance();
    }

    public FirebaseDatabase getFirebaseDatabase(){
        return mFBDB;
    }

    public void reportAction(final String action){
        DeviceActivity deviceActivity = new DeviceActivity(action,(new Date()).getTime(),true);
        DatabaseReference myRef = mFBDB.getReference("activity");
        myRef.child(Utilities.getUniquePsuedoID()).push().setValue(deviceActivity);
    }

    public void reportPackage(final DevicePackage devicePackage, final String packageName){
        DatabaseReference myRef = mFBDB.getReference("application");
        Log.i(TAG,"Package name: "+Utilities.encodeAsFirebaseKey(packageName));
        myRef.child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(packageName)).setValue(devicePackage);
    }

    public DatabaseReference getAppPackageData(final String packageName){
        DatabaseReference myRef = mFBDB.getReference("application");
        Log.i(TAG,"Package name: "+Utilities.encodeAsFirebaseKey(packageName));
        return myRef.child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(packageName));
    }

    public void reportLocation(final DeviceLocation deviceLocation){
        DatabaseReference myRef = mFBDB.getReference("location");
        myRef.child(Utilities.getUniquePsuedoID()).push().setValue(deviceLocation);
    }

    public void syncDeviceConfiguration(final ValueEventListener listener){
        Log.i(TAG, "Setting up refference for device config");
        DatabaseReference myRef = mFBDB.getReference("configuration").child(Utilities.getUniquePsuedoID());
        if(listener!=null) {
            myRef.addValueEventListener(listener);
            myRef.keepSynced(true);
        }
    }

    public void syncDevicePackageInformation(final ValueEventListener listener, String packageName){
        packageName = Utilities.encodeAsFirebaseKey(packageName);
        DatabaseReference myRef = mFBDB.getReference("application").child(Utilities.getUniquePsuedoID()).child(packageName);
        if(listener!=null) {
            myRef.addValueEventListener(listener);
            mPackageListeners.put(packageName, listener);
            myRef.keepSynced(true);
        }
    }

    public void removeDevicePackageListener(String packageName){
        packageName = Utilities.encodeAsFirebaseKey(packageName);
        if(mPackageListeners.get(packageName)!=null) {
            DatabaseReference myRef = mFBDB.getReference("application").child(Utilities.getUniquePsuedoID()).child(packageName);
            myRef.removeEventListener(mPackageListeners.get(packageName));
            mPackageListeners.remove(packageName);
            myRef.keepSynced(false);
        }
    }

}
