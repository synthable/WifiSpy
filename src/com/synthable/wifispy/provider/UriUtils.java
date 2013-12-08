package com.synthable.wifispy.provider;

import android.content.UriMatcher;

public class UriUtils {

    public static final int ACCESS_POINTS = 100;
    public static final int ACCESS_POINT = 101;
    public static final int ACCESS_POINTS_UNTAGGED = 102;

    public static final int TAGS = 200;
    public static final int TAG = 201;
    public static final int TAG_ACCESS_POINTS = 202;

    public static final int ACCESS_POINT_TAG = 301;

    public static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "aps", ACCESS_POINTS);
        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "aps/#", ACCESS_POINT);
        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "aps/untagged", ACCESS_POINTS_UNTAGGED);

        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "tags", TAGS);
        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "tags/#", TAG);
        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "tags/#/aps", TAG_ACCESS_POINTS);

        sUriMatcher.addURI(WifiSpyContract.AUTHORITY, "ap_tags", ACCESS_POINT_TAG);
    }
}
