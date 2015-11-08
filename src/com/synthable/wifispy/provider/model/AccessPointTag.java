package com.synthable.wifispy.provider.model;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPointTags;

import android.content.ContentValues;

public class AccessPointTag {

	private long accessPointId;
	private long tagId;

	public AccessPointTag() {
	}

	public AccessPointTag(long accessPointId, long tagId) {
		this.accessPointId = accessPointId;
		this.tagId = tagId;
	}

	public ContentValues toContentValues() {
		ContentValues v = new ContentValues();
		v.put(AccessPointTags.Columns.ACCESS_POINT_ID, accessPointId);
		v.put(AccessPointTags.Columns.TAG_ID, tagId);
		return v;
	}
}
