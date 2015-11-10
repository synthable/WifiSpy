package com.synthable.wifispy.provider.model;

import com.synthable.wifispy.provider.DbContract.Tags;

import android.content.ContentValues;

public class Tag {

    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Tags.Columns._ID, id);
        values.put(Tags.Columns.NAME, name);
        return values;
    }
}