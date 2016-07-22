package com.ariel.guardian.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.ariel.guardian.database.DatabaseContract;
import com.ariel.guardian.database.DatabaseHelper;

/**
 * Created by mikalackis on 10.7.16..
 */
public class ArielGuardianProvider extends ContentProvider {

    public static final String AUTHORITY = "com.ariel.guardian";

    private static final int APPLICATION_ITEM = 1;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.ApplicationEntry.TABLE_NAME + "/*",
                APPLICATION_ITEM);
    }

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mSqlLiteDabatase;

    @Override
    public boolean onCreate() {

        mDbHelper = new DatabaseHelper(getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseContract.ApplicationEntry.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case APPLICATION_ITEM:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DatabaseContract.ApplicationEntry.COLUMN_NAME_APP_PACKAGE + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int code = sUriMatcher.match(uri);
        String tableName = getTableNameFromUriMatchCode(code);
        switch (sUriMatcher.match(uri)) {
            case APPLICATION_ITEM:
                return "vnd.android.cursor.item/" + tableName;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private String getTableNameFromUriMatchCode(int code) {
        switch (code) {
            case APPLICATION_ITEM:
                return DatabaseContract.ApplicationEntry.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Invalid uri match code: " + code);
        }
    }

}
