package com.synthable.wifispy.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;

public class WifiSpyDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "wifispy.db";
    private static final int DATABASE_VERSION = 1;

    public WifiSpyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccessPoints.SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccessPoints.TABLE);
        onCreate(db);
    }
}
