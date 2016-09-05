package com.ariel.guardian.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

/**
 * Created by mikalackis on 6.7.16..
 */
public class LocationManager implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "LocationManager";

    private final int locationUpdateInterval;
    private final int maxLocationUpdates;
    private final int locationAccuracyThreshold;
    private final boolean constantReporting;
    private final LocationManagerListener mListener;
    private final WeakReference<Context> mContext;

    private LocationManager(LocationManagerBuilder builder){
        this.locationUpdateInterval = builder.locationUpdateInterval;
        this.maxLocationUpdates = builder.maxLocationUpdates;
        this.locationAccuracyThreshold = builder.locationAccuracyThreshold;
        this.constantReporting = builder.constantReporting;
        this.mListener = builder.locationManagerListener;
        this.mContext = builder.context;
    }

    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocationUpdate;
    private int mCurrentLocationmode = -1;

    private int mUpdateCount = 0;

    public void initAndStartLocationUpdates() {
        Log.i(TAG, "LocationManager initializing...");
        final Context context = mContext.get();
        if(context!=null) {
            final ContentResolver contentResolver = context.getContentResolver();
            try {
                mCurrentLocationmode = Settings.Secure.getInt(contentResolver,
                        Settings.Secure.LOCATION_MODE);
                if (mCurrentLocationmode != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                            Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, "Unable find location settings.", e);
            }

            buildGoogleApiClient();
        }
        Log.i(TAG, "LocationManager started!");
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Context context = mContext.get();
        if(context!=null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void restartLocationUpdates() {
        Log.i(TAG, "Starting location updates");
        mUpdateCount = 0;
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            onLocationChanged(lastLocation, true);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, getLocationRequest(), this);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest lr = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(locationUpdateInterval);
        if (!constantReporting) {
            lr.setNumUpdates(maxLocationUpdates);
        }
        return lr;
    }

    @Override
    public void onLocationChanged(final Location location) {
        onLocationChanged(location, false);
    }

    private void onLocationChanged(final Location location, boolean fromLastLocation) {
        mLastLocationUpdate = location;
        if (!fromLastLocation) {
            mUpdateCount++;
            mListener.onLocationChanged(location);
        }
        if (mLastLocationUpdate != null) {
            maybeStopLocationUpdates(mLastLocationUpdate.getAccuracy());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        restartLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mListener.onGoogleClientError(connectionResult);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void maybeStopLocationUpdates(float accuracy) {
        // if mUpdateCount, then this is a case we have the last known location. Don't stop in that
        // case.
        if (!constantReporting) {
            if ((mUpdateCount != 0)
                    && (accuracy <= locationAccuracyThreshold || mUpdateCount == maxLocationUpdates)) {
                Log.i(TAG, "Stopping updates");
                stopUpdates();
            }
        } else {
            Log.i(TAG, "Constant reporting in progress");
        }
    }

    public void stopUpdates() {
        // revert previous location settings
        Context context = mContext.get();
        if(context!=null) {
            final ContentResolver contentResolver = context.getContentResolver();
            Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE,
                    mCurrentLocationmode);
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        mListener.doneWithUpdates();
    }

    public interface LocationManagerListener {
        void onLocationChanged(final Location location);
        void doneWithUpdates();
        void onGoogleClientError(final ConnectionResult connectionResult);
    }

    public static class LocationManagerBuilder{
        private int locationUpdateInterval = 5000; // miliseconds
        private int maxLocationUpdates = 1; // number of location updates before stopping
        private int locationAccuracyThreshold = 5; // accuracy threshold in meters
        private boolean constantReporting = false; // constantly report location until stopped
        private LocationManagerListener locationManagerListener; // required
        private WeakReference<Context> context; // required

        public LocationManagerBuilder(LocationManagerListener locationManagerListener, Context context){
            this.locationManagerListener = locationManagerListener;
            this.context = new WeakReference<Context>(context);
        }

        public LocationManagerBuilder locationUpdateInterval(int locationUpdateInterval){
            this.locationUpdateInterval = locationUpdateInterval;
            return this;
        }

        public LocationManagerBuilder maxLocationUpdates(int maxLocationUpdates){
            this.maxLocationUpdates = maxLocationUpdates;
            return this;
        }

        public LocationManagerBuilder locationAccuracyThreshold(int locationAccuracyThreshold){
            this.locationAccuracyThreshold = locationAccuracyThreshold;
            return this;
        }

        public LocationManagerBuilder constantReporting(boolean constantReporting){
            this.constantReporting = constantReporting;
            return this;
        }

        public LocationManager build(){
            LocationManager locationManager = new LocationManager(this);
            if(locationManager.locationUpdateInterval<5000){
                throw new IllegalStateException("Minimum location update interval is 5000 miliseconds");
            }
            if(locationManager.maxLocationUpdates<1){
                throw new IllegalStateException("Minimum number of location updates is 1");
            }
            if(locationManager.locationAccuracyThreshold<1){
                throw new IllegalStateException("Minimum threshold in meters is 1");
            }
            return new LocationManager(this);
        }

    }

}
