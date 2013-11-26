package com.synthable.wifispy.provider.model;

import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.WifiSpyContract.Tags.Columns;

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
