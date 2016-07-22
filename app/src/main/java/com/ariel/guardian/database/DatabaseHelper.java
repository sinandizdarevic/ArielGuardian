package com.ariel.guardian.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mikalackis on 10.7.16..
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DROP_TABLE_SQL_FORMAT = "DROP TABLE IF EXISTS %s;";

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try{
            createDbTable(db, DatabaseContract.ApplicationEntry.CREATE_TABLE);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeVersion = oldVersion;
    }

    private void createDbTable(SQLiteDatabase db, String createSQL) {
        db.execSQL(createSQL);
    }

    private void dropDbTable(SQLiteDatabase db, String tableName) {
        String dropTableSql = String.format(DROP_TABLE_SQL_FORMAT, tableName);
        db.execSQL(dropTableSql);
    }
}
