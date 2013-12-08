package com.synthable.wifispy.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WifiSpyContract {

	public static final String AUTHORITY = "com.synthable.wifispy";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class AccessPoints {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/aps");
        public static final Uri ACCESSP_POINT_URI = Uri.parse(CONTENT_URI + "/aps/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.wifispy.ap";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wifispy.ap";

        public static final String TABLE = "access_points";

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

        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
            + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.BSSID + " TEXT UNIQUE,"
                + Columns.SSID + " TEXT,"
                + Columns.CAPABILITIES + " TEXT,"
                + Columns.FREQUENCY + " INTEGER,"
                + Columns.STRENGHT + " INTEGER,"
                + Columns.LAT + " FLOAT,"
                + Columns.LNG + " FLOAT"
            +");";

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

    public static final class Tags {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/tags");
        public static final Uri TAG_URI = Uri.parse(CONTENT_URI + "/tags/#");
        public static final Uri TAG_ACCESS_POINTS_URI = Uri.parse(CONTENT_URI + "/tags/#/aps");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.wifispy.tags";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wifispy.tag";

        public static final String TABLE = "tags";

        public static final Uri buildTagUri(long id) {
            return Uri.withAppendedPath(URI, String.valueOf(id));
        }

        public static final Uri buildTagApsUri(long id) {
            return Uri.withAppendedPath(URI, String.valueOf(id))
            	.buildUpon()
            	.appendPath("aps")
            	.build();
        }

        public static final String[] PROJECTION = {
            Columns._ID,
            Columns.NAME
        };

        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
            + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.NAME + " TEXT"
            +");";

        public static final class Columns implements BaseColumns {
            public static final String NAME = "name";
        }
    }

    public static final class AccessPointTags {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/ap_tags");
        public static final Uri TAG_URI = Uri.parse(CONTENT_URI + "/ap_tags/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.wifispy.ap_tags";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wifispy.ap_tag";

        public static final String TABLE = "access_point_tags";

        public static final Uri buildApTagsUri(long id) {
        	return Uri.withAppendedPath(TAG_URI, String.valueOf(id));
        }

        public static final String[] PROJECTION = {
            Columns._ID,
            Columns.ACCESS_POINT_ID,
            Columns.TAG_ID
        };

        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
            + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.ACCESS_POINT_ID + " INTEGER,"
                + Columns.TAG_ID + " INTEGER,"
                + "UNIQUE("+ Columns.ACCESS_POINT_ID +","+ Columns.TAG_ID +"),"
                + "FOREIGN KEY("+ Columns.TAG_ID +") REFERENCES "+ Tags.TABLE +"("+ Tags.Columns._ID+"),"
                + "FOREIGN KEY("+ Columns.ACCESS_POINT_ID +") REFERENCES "+ AccessPoints.TABLE +"("+ AccessPoints.Columns._ID+")"
            +");";

        public static final class Columns implements BaseColumns {
            public static final String ACCESS_POINT_ID = "access_point_id";
            public static final String TAG_ID = "tag_id";
        }
    }
}
