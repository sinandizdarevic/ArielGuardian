package com.ariel.guardian.services;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;

/**
 * Created by mikalackis on 7.6.16..
 */
public class FirebaseAuthService extends ArielService {

    private static final String TAG = "FirebaseAuthService";

    //private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        //mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "FirebaseAuthService attempt login");
//        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                if(authResult.getUser().isAnonymous()){
//                    Log.i(TAG, "Logged in: "+authResult.getUser().getUid());
//                    //FirebaseHelper.getInstance().syncDeviceConfiguration(new DeviceConfigurationValueEventListener());
//                    /*
//                      Start device config service
//                     */
//                    startService(DeviceConfigService.getCallingIntent());
//                }
//                else{
//                    Log.i(TAG, "User not anonymous!!!");
//                }
//                stopSelf();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i(TAG, "EXCEPTION: "+e.getMessage());
//                e.printStackTrace();
//                mArielJobScheduler.registerNewJob(new RetryLoginJobService());
//                stopSelf();
//            }
//        });
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "FirebaseAuthService destroyed");
        //mAuth = null;
    }

    @Override
    String getServiceName() {
        return "FirebaseAuthService";
    }

//    @Override
//    public void injectComponent(GuardianComponent component) {
//        component.inject(this);
//    }
}
