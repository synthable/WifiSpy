package com.synthable.wifispy.provider;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPointTags;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;

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
                case UriUtils.ACCESS_POINTS: {
                    notifyUri = AccessPoints.URI;
                    rowId = db.insertOrThrow(AccessPoints.TABLE, null, values);
                    break;
                }
                case UriUtils.TAGS: {
                    notifyUri = Tags.URI;
                    rowId = db.insertOrThrow(Tags.TABLE, null, values);
                    break;
                }
                case UriUtils.ACCESS_POINT_TAG: {
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
            /** The AccessPoint is probably already in the database. **/
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
                selection = AccessPoints.TABLE +"."+ AccessPoints.Columns._ID +"=?";
                selectionArgs = new String[] {
                	uri.getLastPathSegment()
                };
                break;
            case UriUtils.ACCESS_POINT_TAG:
                qb.setTables(AccessPointTags.TABLE);
                break;
            case UriUtils.ACCESS_POINT_TAGS:
            	Cursor c2 = mDb.rawQuery("SELECT" +
            		" tags._id," +
            		" tags.name" +
            		" FROM tags" +
            		" INNER JOIN access_point_tags ON access_point_tags.access_point_id = access_points._id" +
            		" INNER JOIN access_points ON access_point_tags.access_point_id = access_points._id" +
            		" WHERE access_point_tags.access_point_id = ?",
            		new String[] {
            			uri.getPathSegments().get(1)
            		});
            	c2.setNotificationUri(getContext().getContentResolver(), uri);
            	return c2;
            case UriUtils.TAGS:
            	qb.setTables(Tags.TABLE);
            	break;
            case UriUtils.TAG_ACCESS_POINTS:
            	/**
            	 * TODOD: Take the time to implement this correctly using the QuieryBuilder
            	 */
            	Cursor c = mDb.rawQuery("SELECT" +
            		" access_points._id," +
            		" access_points.bssid," +
            		" access_points.ssid" +
            		" FROM access_points" +
            		" INNER JOIN access_point_tags ON access_point_tags.access_point_id = access_points._id" +
            		" WHERE access_point_tags.tag_id = ?",
            		new String[] {
            			uri.getPathSegments().get(1)
            		});
            	c.setNotificationUri(getContext().getContentResolver(), uri);
            	return c;
            	/*qb.setTables(
            		AccessPointTags.TABLE +" INNER JOIN "+ Tags.TABLE +" ON Tags."+ Tags.Columns._ID +" = AccessPointTags."+ AccessPointTags.Columns.TAG_ID
            		+ " INNER JOIN "+ AccessPoints.TABLE +" ON AccessPoints."+ AccessPoints.Columns._ID +" = PointTags."+ AccessPointTags.Columns.ACCESS_POINT_ID
            	);*/
            	/*qb.setTables(AccessPointTags.TABLE +","+ AccessPoints.TABLE);
            	selection = AccessPointTags.TABLE +"."+ AccessPointTags.Columns.TAG_ID +" = ?";
            	selectionArgs = new String[] {
            		uri.getPathSegments().get(1)
            	};
            	projection = new String[]{
        			AccessPoints.TABLE +"."+ AccessPoints.Columns._ID,
        			AccessPoints.TABLE +"."+ AccessPoints.Columns.BSSID,
        			AccessPoints.TABLE +"."+ AccessPoints.Columns.SSID
            	};
            	break;*/
            case UriUtils.ACCESS_POINTS_UNTAGGED:
            	Cursor untagged = mDb.rawQuery("SELECT" +
            		" access_points._id," +
            		" access_points.bssid," +
            		" access_points.ssid" +
            		" FROM access_points" +
            		" LEFT JOIN access_point_tags ON access_point_tags.access_point_id = access_points._id" +
            		" WHERE access_points._id NOT IN (SELECT access_point_tags.access_point_id" +
            			" FROM access_point_tags" +
            		")", null);
        		untagged.setNotificationUri(getContext().getContentResolver(), uri);
            	return untagged;
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