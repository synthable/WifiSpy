package com.synthable.wifispy.provider;

import android.content.UriMatcher;

public class UriUtils {

    public static final int TAGS = 100;
    public static final int TAG = 101;

    public static final int ACCESS_POINTS = 200;
    public static final int ACCESS_POINT = 201;

    public static final int AP_TAGS = 300;
    public static final int AP_TAG = 301;

    public static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(DbContract.AUTHORITY, "tags", TAGS);
        sUriMatcher.addURI(DbContract.AUTHORITY, "tags/#", TAG);

        sUriMatcher.addURI(DbContract.AUTHORITY, "aps", ACCESS_POINTS);
        sUriMatcher.addURI(DbContract.AUTHORITY, "aps/#", ACCESS_POINT);

        sUriMatcher.addURI(DbContract.AUTHORITY, "ap_tags", AP_TAGS);
        sUriMatcher.addURI(DbContract.AUTHORITY, "ap_tags/#", AP_TAG);
    }
}
