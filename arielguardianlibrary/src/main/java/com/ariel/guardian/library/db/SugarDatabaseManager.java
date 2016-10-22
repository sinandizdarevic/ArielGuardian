package com.ariel.guardian.library.db;

import android.content.Context;
import android.util.Log;

import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by mikalackis on 14.10.16..
 */

public class SugarDatabaseManager implements ArielDatabaseInterface {

    public static final String TAG = "SugarDatabaseManager";

    private static SugarDatabaseManager mInstance;

    public static SugarDatabaseManager getInstance(final Context context){
        if(mInstance==null){
            mInstance = new SugarDatabaseManager(context);
        }
        return mInstance;
    }

    private SugarDatabaseManager(final Context context){
        SugarContext.init(context);
    }

    @Override
    public long createOrUpdateObject(SugarRecord record) {
        Log.i(TAG, "Saving object of type: "+record.getClass().getCanonicalName());
        return record.save();
    }

    @Override
    public void removeObject(SugarRecord record) {
        Log.i(TAG, "Removing object of type: "+record.getClass().getCanonicalName());
        record.delete();
    }

    @Override
    public SugarRecord getObjectById(Class<? extends SugarRecord> type, long id) {
        Log.i(TAG, "Retrieve object of type: "+type.getClass().getCanonicalName()+", with ID: "+id);
        return SugarRecord.findById(type, id);
    }

    @Override
    public SugarRecord getObjectByField(Class<? extends SugarRecord> type, String field, String value) {
        List<SugarRecord> list = (List<SugarRecord>)SugarRecord.find(type, field + " = ?", value);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<? extends SugarRecord> getListOfObjects(Class<? extends SugarRecord> type) {
        Log.i(TAG, "Retrieve list of objects of type: "+type.getClass().getCanonicalName());
        return SugarRecord.listAll(type);
    }

}
