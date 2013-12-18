package com.synthable.wifispy;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.TagsAdapter;
import com.synthable.wifispy.provider.model.AccessPoint;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class AccessPointActivity extends Activity implements
	LoaderCallbacks<Cursor> {

	private static final int LOADER_AP = 0;
	private static final int LOADER_TAGS = 1;

	private static RequestQueue sRequestQueue;
	private static ImageLoader sImageLoader;

	private int mAccessPointId;
	private AccessPoint mAccessPoint;
	private TextView mSsid;
	private TextView mBssid;
	private NetworkImageView mMapImage;
	private TagsAdapter mTagsAdapter;
	private ListView mTagsList;

	private static final String sUrl = 
		"http://maps.google.com/maps/api/staticmap?center=%s,%s&zoom=16&size=250x200&sensor=false";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_access_point);

		sImageLoader = new ImageLoader(
			Volley.newRequestQueue(this),
			new BitmapLruImageCache(1)
		);

		mSsid = (TextView) findViewById(R.id.ssid);
		mBssid = (TextView) findViewById(R.id.bssid);
		mMapImage = (NetworkImageView) findViewById(R.id.static_map);

		Uri uri = getIntent().getData();
		mAccessPointId = Integer.valueOf(uri.getLastPathSegment());

		mTagsAdapter = new TagsAdapter(this, null);
		mTagsList = (ListView) findViewById(android.R.id.list);

		mTagsList.setAdapter(mTagsAdapter);

		getLoaderManager().initLoader(LOADER_AP, null, this);
		getLoaderManager().initLoader(LOADER_TAGS, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.access_point, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch(id) {
			case LOADER_AP:
				return new CursorLoader(this, AccessPoints.buildApUri(mAccessPointId), AccessPoints.PROJECTION, null, null, null);
			case LOADER_TAGS:
				return new CursorLoader(this, AccessPoints.buildApTagsUri(mAccessPointId), Tags.PROJECTION, null, null, null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()) {
			case LOADER_AP:
				mAccessPoint = new AccessPoint(cursor);
				refreshView();
			break;
			case LOADER_TAGS:
				mTagsAdapter.swapCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()) {
			case LOADER_TAGS:
				mTagsAdapter.swapCursor(null);
			break;
		}
	}

	private void refreshView() {
		mSsid.setText(mAccessPoint.getSsid());
		mBssid.setText(mAccessPoint.getBssid());
		Log.v("STRING", String.format(sUrl, mAccessPoint.getLat()/1E6, mAccessPoint.getLng()/1E6));
		mMapImage.setImageUrl(String.format(sUrl, mAccessPoint.getLat()/1E6, mAccessPoint.getLng()/1E6), sImageLoader);
	}
}
