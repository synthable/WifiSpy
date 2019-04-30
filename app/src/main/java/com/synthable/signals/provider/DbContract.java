package com.synthable.signals.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {

    public static final String AUTHORITY = "com.synthable.signals";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Tags {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/tags");
        public static final Uri TAG_URI = Uri.parse(CONTENT_URI + "/tags/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.signals.tags";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.signals.tag";

        public static final Uri buildTagUri(long id) {
            return Uri.withAppendedPath(TAG_URI, String.valueOf(id));
        }

        public static final String[] PROJECTION = {
                Columns._ID,
                Columns.NAME
        };

        public static final String[] DIALOG_PROJECTION = {
                Columns._ID,
                Columns.NAME,
                "0 AS checked"
        };

        public static final String TABLE = "tags";
        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
                + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.NAME + " TEXT"
                + ");";

        public static final class Columns implements BaseColumns {
            public static final String NAME = "name";
        }
    }

    public static final class AccessPoints {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/aps");
        public static final Uri ACCESSP_POINT_URI = Uri.parse(CONTENT_URI + "/aps/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.signals.aps";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.signals.ap";

        public static final Uri buildApUri(long id) {
            return Uri.withAppendedPath(URI, String.valueOf(id));
        }

        public static final String[] PROJECTION = {
                Columns._ID,
                Columns.BSSID,
                Columns.SSID,
                Columns.CAPABILITIES,
                Columns.FREQUENCY,
                Columns.STRENGHT,
                Columns.LAT,
                Columns.LNG
        };

        public static final String TABLE = "access_points";
        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
                + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.BSSID + " TEXT UNIQUE,"
                + Columns.SSID + " TEXT,"
                + Columns.CAPABILITIES + " TEXT,"
                + Columns.FREQUENCY + " INTEGER,"
                + Columns.STRENGHT + " INTEGER,"
                + Columns.LAT + " INTEGER,"
                + Columns.LNG + " INTEGER"
                + ");";

        public static final class Columns implements BaseColumns {
            public static final String BSSID = "bssid";
            public static final String SSID = "ssid";
            public static final String CAPABILITIES = "capabilities";
            public static final String FREQUENCY = "frequency";
            public static final String STRENGHT = "strength";
            public static final String LAT = "latitude";
            public static final String LNG = "longitude";
        }
    }

    public static final class AccessPointTags {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/ap_tags");
        public static final Uri COUNT_URI = Uri.parse(CONTENT_URI + "/ap_tags/count");
        public static final Uri TAG_URI = Uri.parse(CONTENT_URI + "/ap_tags/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.signals.ap_tags";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.signals.ap_tag";

        public static final Uri buildApTagsUri(long id) {
            return Uri.withAppendedPath(URI, String.valueOf(id));
        }

        public static final String[] PROJECTION = {
                Columns._ID,
                Columns.ACCESS_POINT_ID,
                Columns.TAG_ID
        };

        public static final String COUNT_QUERY = new StringBuilder("SELECT COUNT(*)")
                .append(" FROM "+ AccessPointTags.TABLE)
                .append(" WHERE ")
                .append(AccessPointTags.TABLE +"."+ AccessPointTags.Columns.TAG_ID)
                .append(" = ")
                .append(Tags.TABLE +"."+ Tags.Columns._ID)
                .toString();

        public static final String TABLE = "access_point_tags";
        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
                + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.ACCESS_POINT_ID + " TEXT,"
                + Columns.TAG_ID + " INTEGER,"
                + "UNIQUE(" + Columns.ACCESS_POINT_ID + "," + Columns.TAG_ID + "),"
                    + "FOREIGN KEY(" + Columns.TAG_ID + ") REFERENCES " + Tags.TABLE + "(" + Tags.Columns._ID + "),"
                    + "FOREIGN KEY(" + Columns.ACCESS_POINT_ID + ") REFERENCES " + AccessPoints.TABLE + "(" + AccessPoints.Columns.BSSID + ")"
                + ");";

        public static final class Columns implements BaseColumns {
            public static final String ACCESS_POINT_ID = "access_point_id";
            public static final String TAG_ID = "tag_id";
        }
    }
}