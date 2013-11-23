package com.synthable.wifispy;

import com.synthable.wifispy.provider.WifiSpyContract.Tags;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class TagsAdapter extends SimpleCursorAdapter {

	private static final String[] FROM = new String[] {
		Tags.Columns.NAME
	};
	private static final int[] TO = new int[] {
		android.R.id.text1
	};

	public TagsAdapter(Context context, Cursor c) {
		super(context, android.R.layout.simple_list_item_1, c, FROM, TO);
	}

}