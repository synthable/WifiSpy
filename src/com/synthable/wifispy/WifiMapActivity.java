package com.synthable.wifispy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;

public class WifiMapActivity extends Activity implements
	LoaderCallbacks<Cursor> {

	private GoogleMap mMap;
	private String mApId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_map);

		mApId = getIntent().getData().getLastPathSegment();

		getLoaderManager().initLoader(0, null, this);

		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mMap = f.getMap();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mMap.setMyLocationEnabled(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = AccessPoints.buildApUri(Long.valueOf(mApId));
		return new CursorLoader(this, uri, AccessPoints.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		final AccessPoint ap = new AccessPoint(cursor);
		final LatLng point = new LatLng(ap.getLat() / 1E6, ap.getLng() / 1E6);

		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 15);
		mMap.animateCamera(cameraUpdate, 1750, new GoogleMap.CancelableCallback() {
			@Override
			public void onFinish() {
				mMap.addMarker(new MarkerOptions()
					.position(point)
					.title(ap.getSsid())
				);
			}
			@Override
			public void onCancel() {
			}
		});
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
