package com.ariel.guardian.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.ariel.guardian.provider.ArielGuardianProvider;

/**
 * Created by mikalackis on 10.7.16..
 */
public final class DatabaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "database.db";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";

    public DatabaseContract(){}

    public static abstract class ApplicationEntry implements BaseColumns {
        public static final String TABLE_NAME = "app_entry";
        public static final String COLUMN_NAME_APP_PACKAGE = "appPackage";
        public static final String COLUMN_NAME_APP_DISABLED = "appDisabled"; // 0 - false, 1 - true

        public static final Uri CONTENT_URI = Uri.parse("content://"+ ArielGuardianProvider.AUTHORITY+"/"+TABLE_NAME);

        public static final String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME +" (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_APP_PACKAGE + " TEXT UNIQUE ON CONFLICT REPLACE," +
                COLUMN_NAME_APP_DISABLED + " INTEGER DEFAULT 0" +
                ");)";
    }

}
