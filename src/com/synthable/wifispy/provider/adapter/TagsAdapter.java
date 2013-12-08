package com.synthable.wifispy.provider.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;

public class TagsAdapter extends SimpleCursorAdapter {

	private static final String[] FROM = new String[] {
		Tags.Columns.NAME
	};
	private static final int[] TO = new int[] {
		R.id.tag_name
	};

	public TagsAdapter(Context context, Cursor c) {
		super(context, R.layout.tag_list_item, c, FROM, TO);
	}

	public TagsAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c, FROM, new int[] {
			android.R.id.text1
		});
	}
}
