package com.synthable.wifispy.provider;

import android.content.UriMatcher;

public class UriUtils {

    public static final int TAGS = 100;
    public static final int TAG = 101;

    public static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(DbContract.AUTHORITY, "tags", TAGS);
        sUriMatcher.addURI(DbContract.AUTHORITY, "tags/#", TAG);
    }
}
