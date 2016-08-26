package com.ariel.guardian.firebase.listeners;

/**
 * Created by mikalackis on 25.8.16..
 */
public interface DataLoadCompletedListener {

    void onDataLoadCompleted();
    void onDataLoadError(final String errorMessage);

}
