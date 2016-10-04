package com.ariel.guardian.firebase;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.ariel.guardian.library.firebase.model.DeviceActivity;
import com.ariel.guardian.library.firebase.model.DeviceLocation;
import com.ariel.guardian.library.firebase.model.DeviceApplication;
import com.ariel.guardian.library.firebase.model.QRCode;
import com.ariel.guardian.library.utils.ArielUtilities;
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

    public static final String FB_APPLICATION = "application";
    public static final String FB_CONFIGURATION = "configuration";
    public static final String FB_LOCATION = "location";
    public static final String FB_ACTIVITY = "activity";
    public static final String FB_SETTINGS = "settings";
    public static final String FB_QR_CODES = "qrcodes";


    private FirebaseDatabase mFBDB;

    private ArrayMap<String, ValueEventListener> mPackageListeners = new ArrayMap<String, ValueEventListener>();

    public FirebaseHelper(){
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
        DatabaseReference myRef = mFBDB.getReference(FB_ACTIVITY);
        myRef.child(ArielUtilities.getUniquePseudoID()).push().setValue(deviceActivity);
    }

    /**
     * Reports any update regarding particular application (install, remove, update)
     * @param deviceApplication class holding config information about application package
     * @param packageName actual package name of the application
     */
    public void reportApplication(final DeviceApplication deviceApplication, final String packageName){
        DatabaseReference myRef = mFBDB.getReference(FB_APPLICATION);
        myRef.child(ArielUtilities.getUniquePseudoID()).child(""+packageName.hashCode()).setValue(deviceApplication);
    }

    /**
     * Retrieves package configuration for particular application
     * @param packageName application package name
     * @return Firebase database reference to application object
     */
    public DatabaseReference getAppPackageData(final String packageName){
        DatabaseReference myRef = mFBDB.getReference(FB_APPLICATION);
        return myRef.child(ArielUtilities.getUniquePseudoID()).child(""+packageName.hashCode());
    }

    /**
     * Reports device location to firebase
     * @param deviceLocation device location object
     */
    public void reportLocation(final DeviceLocation deviceLocation){
        DatabaseReference myRef = mFBDB.getReference(FB_LOCATION);
        myRef.child(ArielUtilities.getUniquePseudoID()).push().setValue(deviceLocation);
    }

    /**
     * Reports device activating qrcode to firebase
     * @param qrcode QRCode object
     */
    public void reportQRCode(final QRCode qrcode){
        DatabaseReference myRef = mFBDB.getReference(FB_QR_CODES);
        myRef.child(ArielUtilities.getUniquePseudoID()).push().setValue(qrcode);
    }

    public DatabaseReference getApplicationReference(final String deviceId){
        return mFBDB.getReference(FB_APPLICATION).child(deviceId);
    }

    public DatabaseReference getLocationReference(final String deviceId){
        return mFBDB.getReference(FB_LOCATION).child(deviceId);
    }

    public DatabaseReference getConfigurationReference(final String deviceId){
        return mFBDB.getReference(FB_CONFIGURATION).child(deviceId);
    }

    public DatabaseReference getSettingsReference(){
        return mFBDB.getReference(FB_SETTINGS);
    }

    public void syncDevicePackageInformation(final ValueEventListener listener, String packageName){
        packageName = ArielUtilities.encodeAsFirebaseKey(packageName);
        DatabaseReference myRef = mFBDB.getReference("application").child(ArielUtilities.getUniquePseudoID()).child(""+packageName.hashCode());
        if(listener!=null) {
            myRef.addValueEventListener(listener);
            mPackageListeners.put(packageName, listener);
            myRef.keepSynced(true);
        }
    }

    public void removeDevicePackageListener(String packageName){
        packageName = ArielUtilities.encodeAsFirebaseKey(packageName);
        if(mPackageListeners.get(packageName)!=null) {
            DatabaseReference myRef = mFBDB.getReference("application").child(ArielUtilities.getUniquePseudoID()).child(""+packageName.hashCode());
            myRef.removeEventListener(mPackageListeners.get(packageName));
            mPackageListeners.remove(packageName);
            myRef.keepSynced(false);
        }
    }

}
