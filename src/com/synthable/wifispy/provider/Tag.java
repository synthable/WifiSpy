package com.synthable.wifispy.provider;

import com.synthable.wifispy.provider.WifiSpyContract.Tags;

import android.content.ContentValues;

public class Tag {

	private String name;

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(Tags.Columns.NAME, name);
		return values;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
