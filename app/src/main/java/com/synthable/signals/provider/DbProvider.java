package com.synthable.signals.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.synthable.signals.provider.DbContract.AccessPointTags;
import com.synthable.signals.provider.DbContract.AccessPoints;
import com.synthable.signals.provider.DbContract.Tags;

public class DbProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.synthable.signals");

    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (UriUtils.sUriMatcher.match(uri)) {
            case UriUtils.TAG: {
                String id = uri.getLastPathSegment();
                count = db.delete(Tags.TABLE, Tags.Columns._ID + "=" + id, null);
                break;
            }
            case UriUtils.ACCESS_POINT: {
                String id = uri.getLastPathSegment();
                count = db.delete(AccessPoints.TABLE, AccessPoints.Columns._ID + "=" + id, null);
                break;
            }
            case UriUtils.AP_TAG: {
                String id = uri.getLastPathSegment();
                count = db.delete(AccessPointTags.TABLE, AccessPointTags.Columns._ID + "=" + id, null);
                uri = AccessPointTags.URI;
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
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
                case UriUtils.TAGS: {
                    notifyUri = Tags.URI;
                    rowId = db.insertWithOnConflict(Tags.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    break;
                }
                case UriUtils.ACCESS_POINTS: {
                    notifyUri = AccessPoints.URI;
                    rowId = db.insertWithOnConflict(AccessPoints.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    break;
                }
                case UriUtils.AP_TAGS: {
                    notifyUri = AccessPointTags.URI;
                    rowId = db.insertWithOnConflict(AccessPointTags.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
            /** TODO: Handle this exception **/
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String groupBy = null;

        switch (UriUtils.sUriMatcher.match(uri)) {
            case UriUtils.TAGS:
                qb.setTables(Tags.TABLE);
                break;
            case UriUtils.AP_TAGS_COUNT:
                String tagsCountSubQuery = AccessPointTags.COUNT_QUERY;

                qb.setTables(Tags.TABLE +","+ AccessPointTags.TABLE);
                projection = new String[] {
                    "tags._id AS _id", "name", "("+ tagsCountSubQuery +") AS _count"
                };
                groupBy = "tags._id";
                break;
            case UriUtils.ACCESS_POINTS:
                qb.setTables(AccessPoints.TABLE);
                break;
            case UriUtils.AP_TAGS:
                qb.setTables(AccessPointTags.TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown query URI: " + uri);
        }

        Cursor c = qb.query(mDb, projection, selection, selectionArgs, groupBy, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch(UriUtils.sUriMatcher.match(uri)) {
            default:
                throw new IllegalArgumentException("Unknown update URI " + uri);
        }
    }
}