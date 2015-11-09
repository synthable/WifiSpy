package com.synthable.wifispy.provider.model;

import com.synthable.wifispy.provider.DbContract.Tags;

import android.content.ContentValues;

public class Tag {

    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tags.Columns.NAME, name);
        return values;
    }
}