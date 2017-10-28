package com.synthable.wifispy.provider.model;

import android.content.ContentValues;

import com.synthable.wifispy.provider.DbContract.AccessPointTags;

public class AccessPointTag {

    private Long id;
    private String apBssid;
    private Long tagId;

    public AccessPointTag(String apBssid, Long tagId) {
        this.apBssid = apBssid;
        this.tagId = tagId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApBssid() {
        return apBssid;
    }

    public void setApBssid(String apBssid) {
        this.apBssid = apBssid;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(AccessPointTags.Columns._ID, id);
        values.put(AccessPointTags.Columns.ACCESS_POINT_ID, apBssid);
        values.put(AccessPointTags.Columns.TAG_ID, tagId);
        return values;
    }
}