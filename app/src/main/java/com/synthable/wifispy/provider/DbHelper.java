package com.synthable.wifispy.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synthable.wifispy.provider.DbContract.AccessPointTags;
import com.synthable.wifispy.provider.DbContract.AccessPoints;
import com.synthable.wifispy.provider.DbContract.Tags;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wifispy.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tags.SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tags.TABLE);
        onCreate(db);
    }
}
