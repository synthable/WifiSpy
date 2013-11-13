package com.synthable.wifispy.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class WifiSpyContract {

	public static final String AUTHORITY = "com.synthable.wifispy";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class AccessPoints {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/aps");
        public static final Uri STRAIN_URI = Uri.parse(CONTENT_URI + "/aps/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.wifispy.aps";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wifispy.ap";

        public static final String TABLE = "access_points";

        public static final Uri buildApUri(int id) {
            return Uri.withAppendedPath(URI, String.valueOf(id));
        }

        public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS "
            + TABLE + "("
                + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Columns.BSSID + " TEXT,"  // UNIQUE,"
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

}