package com.ariel.guardian.library.db;

import com.ariel.guardian.library.db.model.DeviceApplication;
import com.ariel.guardian.library.db.model.WrapperMessage;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by mikalackis on 6.10.16..
 */

public interface ArielDatabaseInterface {

    long createOrUpdateObject(final SugarRecord record);

    void removeObject(final SugarRecord record);

    SugarRecord getObjectById(Class<? extends SugarRecord> type, final long id);

    SugarRecord getObjectByField(Class<? extends SugarRecord> type, final String field, String value);

    List<? extends SugarRecord> getListOfObjects(Class<? extends SugarRecord> type);

}
