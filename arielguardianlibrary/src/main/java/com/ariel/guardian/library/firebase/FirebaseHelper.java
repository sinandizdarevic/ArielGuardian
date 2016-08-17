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
 * Main Firebase communication class. Use this class to communicate with Firebase
 * and send data to it.
 */
public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    public static final String GOOGLE_MAPS_URL = "http://maps.google.com/?q=%1$f,%2$f";

    private static FirebaseHelper mInstance;
    private FirebaseDatabase mFBDB;

    private ArrayMap<String, ValueEventListener> mPackageListeners = new ArrayMap<String, ValueEventListener>();

    /**
     *
     * @return current Firebase instance
     */
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

    /**
     *
     * @return FirebaseDatabase object
     */
    public FirebaseDatabase getFirebaseDatabase(){
        return mFBDB;
    }

    /**
     * Method used to inform firebase about action being taken
     * @param action action to be reported to firebase, usually a command
     */
    public void reportAction(final String action){
        DeviceActivity deviceActivity = new DeviceActivity(action,(new Date()).getTime(),true);
        DatabaseReference myRef = mFBDB.getReference("activity");
        myRef.child(Utilities.getUniquePsuedoID()).push().setValue(deviceActivity);
    }

    /**
     * Reports any update regarding particular application (install, remove, update)
     * @param devicePackage class holding config information about application package
     * @param packageName actual package name of the application
     */
    public void reportPackage(final DevicePackage devicePackage, final String packageName){
        DatabaseReference myRef = mFBDB.getReference("application");
        Log.i(TAG,"Package name: "+Utilities.encodeAsFirebaseKey(packageName));
        myRef.child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(packageName)).setValue(devicePackage);
    }

    /**
     * Retrieves package configuration for particular application
     * @param packageName application package name
     * @return Firebase database reference to application object
     */
    public DatabaseReference getAppPackageData(final String packageName){
        DatabaseReference myRef = mFBDB.getReference("application");
        Log.i(TAG,"Package name: "+Utilities.encodeAsFirebaseKey(packageName));
        return myRef.child(Utilities.getUniquePsuedoID()).child(Utilities.encodeAsFirebaseKey(packageName));
    }

    /**
     * Reports device location to firebase
     * @param deviceLocation device location object
     */
    public void reportLocation(final DeviceLocation deviceLocation){
        DatabaseReference myRef = mFBDB.getReference("location");
        myRef.child(Utilities.getUniquePsuedoID()).push().setValue(deviceLocation);
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
