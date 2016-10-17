package com.ariel.guardian.library.db;

import com.ariel.guardian.library.db.realm.model.WrapperMessage;
import com.ariel.guardian.library.db.realm.model.DeviceApplication;

import java.util.List;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielDatabaseInterface {

    // realm methods
    long createOrUpdateApplication(final DeviceApplication object);

    long saveWrapperMessage(final WrapperMessage message);

    void removeWrapperMessage(final long id);

    List<DeviceApplication> getDeviceApplications(final boolean managed);

    WrapperMessage getUnmanagedWrapperMessageById(final long id);

    DeviceApplication getDeviceApplicationById(final long id, final boolean managed);

}
