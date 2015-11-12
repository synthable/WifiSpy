package com.synthable.wifispy.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synthable.wifispy.provider.DbContract.Tags;
import com.synthable.wifispy.provider.DbContract.AccessPoints;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wifispy.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tags.SCHEMA);
        db.execSQL("INSERT INTO " + Tags.TABLE + " VALUES(null, 'Default')");

        db.execSQL(AccessPoints.SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tags.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AccessPoints.TABLE);
        onCreate(db);
    }
}
