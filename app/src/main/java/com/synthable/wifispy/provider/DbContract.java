package com.synthable.wifispy.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DbContract {

    public static final String AUTHORITY = "com.synthable.wifispy";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Tags {
        public static final Uri URI = Uri.parse(CONTENT_URI + "/tags");
        public static final Uri TAG_URI = Uri.parse(CONTENT_URI + "/tags/#");

        public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.wifispy.tags";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.wifispy.tag";

        public static final Uri buildTagUri(long id) {
            return Uri.withAppendedPath(TAG_URI, String.valueOf(id));
        }

        public static final String[] PROJECTION = {
                Columns._ID,
                Columns.NAME
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
}