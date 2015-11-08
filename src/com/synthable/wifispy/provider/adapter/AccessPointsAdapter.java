package com.synthable.wifispy.provider.adapter;

import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints.Columns;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class AccessPointsAdapter extends SimpleCursorAdapter {

	private static final String[] FROM = new String[] {
		AccessPoints.Columns.SSID, AccessPoints.Columns.BSSID
	};

	private static final int[] TO = new int[] {
		R.id.ssid, R.id.bssid
	};

	public AccessPointsAdapter(Context context, Cursor c) {
		super(context, R.layout.access_point_list_item, c, FROM, TO);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);

		int strength = cursor.getInt(cursor.getColumnIndex(Columns.STRENGHT)) * -1;
		ImageView image = (ImageView) view.findViewById(R.id.access_point_item_image);

		String strengthImage = "low";
		if(strength <= 75) {
			strengthImage = "full";
		} else if (strength >= 76 && strength < 90) {
			strengthImage = "med";
		}

		image.setImageResource(
			context.getResources().getIdentifier(strengthImage, "drawable", context.getPackageName())
		);
	}
}
