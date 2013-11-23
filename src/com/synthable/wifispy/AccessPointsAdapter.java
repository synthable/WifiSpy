package com.synthable.wifispy;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class AccessPointsAdapter extends SimpleCursorAdapter {

	private static final String[] FROM = new String[] {
		AccessPoints.Columns.SSID, AccessPoints.Columns.BSSID
	};

	private static final int[] TO = new int[] {
		R.id.ssid, R.id.bssid
	};

	public AccessPointsAdapter(Context context, Cursor c) {
		super(context, R.layout.list_item, c, FROM, TO);
	}

}