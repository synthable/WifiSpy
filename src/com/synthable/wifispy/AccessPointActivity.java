package com.synthable.wifispy;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPointTags;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.TagsAdapter;
import com.synthable.wifispy.provider.model.AccessPoint;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.TextView;

public class AccessPointActivity extends Activity implements
	MultiChoiceModeListener,
	LoaderCallbacks<Cursor> {

	private static final int LOADER_AP = 0;
	private static final int LOADER_TAGS = 1;

	private static ImageLoader sImageLoader;

	private int mAccessPointId;
	private AccessPoint mAccessPoint;
	private TextView mSsid;
	private TextView mBssid;
	private TextView mSecurity;
	private TextView mStrength;
	//private NetworkImageView mMapImage;
	private TagsAdapter mTagsAdapter;
	private ListView mTagsList;
	private ArrayList<Long> mCheckedIds = new ArrayList<Long>();

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
		mSecurity = (TextView) findViewById(R.id.security);
		mStrength = (TextView) findViewById(R.id.signal_strength);
		//mMapImage = (NetworkImageView) findViewById(R.id.static_map);

		Uri uri = getIntent().getData();
		mAccessPointId = Integer.valueOf(uri.getLastPathSegment());

		mTagsAdapter = new TagsAdapter(this, null);
		mTagsList = (ListView) findViewById(android.R.id.list);
		mTagsList.setEmptyView(findViewById(android.R.id.empty));

		mTagsList.setAdapter(mTagsAdapter);
		mTagsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mTagsList.setMultiChoiceModeListener(this);

		getLoaderManager().initLoader(LOADER_AP, null, this);
		getLoaderManager().initLoader(LOADER_TAGS, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.access_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_map:
				Uri uri = AccessPoints.buildApUri(mAccessPointId);
				Intent i = new Intent(Intent.ACTION_VIEW, uri, this, WifiMapActivity.class);
				startActivity(i);
			break;
		}

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
		mSecurity.setText(mAccessPoint.getCapabilities());
		mStrength.setText(mAccessPoint.getStrength() +" dBm");
		//mMapImage.setImageUrl(String.format(sUrl, mAccessPoint.getLat()/1E6, mAccessPoint.getLng()/1E6), sImageLoader);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.action_delete:
	        	for(long id : mCheckedIds) {
	        		Uri uri = AccessPointTags.TAG_URI.buildUpon().appendPath(String.valueOf(id)).build();
	        		getContentResolver().delete(uri, null, null);
	        	}
	        	mode.finish();
	            return true;
	        default:
	            return false;
	    }
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.tags_context, menu);
        mode.setTitle("Select Tags");
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mCheckedIds.clear();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		final int checkedCount = mTagsList.getCheckedItemCount();
		switch (checkedCount) {
	        case 0:
	            mode.setSubtitle(null);
	            break;
	        case 1:
	            mode.setSubtitle("One Tag selected");
	            break;
	        default:
	            mode.setSubtitle(checkedCount + " Tags selected");
	            break;
	    }

		if(checked) {
			mCheckedIds.add(id);
		} else {
			mCheckedIds.remove(id);
		}
	}
}
