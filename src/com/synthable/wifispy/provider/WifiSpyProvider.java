package com.synthable.wifispy.provider;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WifiSpyProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri.parse("content://com.synthable.wifispy");

    private WifiSpyDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new WifiSpyDbHelper(getContext());
        return true;
    }

	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
	}

	@Override
    public String getType(Uri uri) {
        return null;
	}

	@Override
    public Uri insert(Uri uri, ContentValues values) throws SQLiteConstraintException {
        try {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long rowId;
            Uri notifyUri;

            switch (UriUtils.sUriMatcher.match(uri)) {
                case UriUtils.ACCESS_POINTS: {
                    notifyUri = AccessPoints.URI;
                    rowId = db.insertOrThrow(AccessPoints.TABLE, null, values);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown insert URI: " + uri);
            }

            if (rowId > 0) {
                Uri insertUri = ContentUris.withAppendedId(notifyUri, rowId);
                getContext().getContentResolver().notifyChange(insertUri, null);
                return insertUri;
            }
        } catch (SQLiteConstraintException e) {
            /** The AccessPoint is probably already in the database. **/
            throw e;
        }
        return null;
	}

	@Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	    final SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
	    final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (UriUtils.sUriMatcher.match(uri)) {
            case UriUtils.ACCESS_POINTS:
                qb.setTables(AccessPoints.TABLE);
                break;
            case UriUtils.ACCESS_POINT:
                qb.setTables(AccessPoints.TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown query URI: " + uri);
        }

        Cursor c = qb.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	    final SQLiteDatabase db = mDbHelper.getWritableDatabase();

	    switch(UriUtils.sUriMatcher.match(uri)) {
	        case UriUtils.ACCESS_POINT:
                if(selection == null) {
                    selection = AccessPoints.Columns._ID +"=?";
                }
                selectionArgs = new String[] {uri.getLastPathSegment()};
                return db.update(AccessPoints.TABLE, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown update URI " + uri);
        }
	}
}