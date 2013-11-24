package com.synthable.wifispy.ui;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.app.ListActivity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.synthable.wifispy.R;
import com.synthable.wifispy.WifiSpyService;
import com.synthable.wifispy.R.id;
import com.synthable.wifispy.R.layout;
import com.synthable.wifispy.R.menu;
import com.synthable.wifispy.R.string;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.AccessPointsAdapter;
import com.synthable.wifispy.provider.adapter.TagsAdapter;

public class MainActivity extends ListActivity implements
	ActionBar.OnNavigationListener,
	LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_TAGS = 0;
	private static final int LOADER_ACCESS_POINTS = 1;

	private ActionBar mActionBar;

	private SimpleCursorAdapter mTagsAdapter;
	private SimpleCursorAdapter mAccessPointsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		mAccessPointsAdapter = new AccessPointsAdapter(this, null);
		mTagsAdapter = new TagsAdapter(this, android.R.layout.simple_list_item_1, null);

		mActionBar.setListNavigationCallbacks(mTagsAdapter, this);
		getListView().setAdapter(mAccessPointsAdapter);

		getLoaderManager().initLoader(LOADER_TAGS, null, this);
		getLoaderManager().initLoader(LOADER_ACCESS_POINTS, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(code != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(code, this, 0, new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
				}
			}).show();
		} else {
			//getLoaderManager().initLoader(LOADER_TAGS, null, this);
			//getLoaderManager().initLoader(LOADER_ACCESS_POINTS, null, this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_service_toggle:
	        	if(WifiSpyService.isRunning) {
	        		WifiSpyService.stop(this);
	        		item.setTitle(getResources().getString(R.string.action_service_start));
	        	} else {
	        		WifiSpyService.start(this);
	        		item.setTitle(getResources().getString(R.string.action_service_stop));
	        	}
	            return true;
	        case R.id.action_tags:
	        	startActivity(new Intent(this, TagsActivity.class));
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(WifiSpyService.isRunning) {
			menu.findItem(R.id.action_service_toggle)
				.setTitle(getResources().getString(R.string.action_service_stop));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Toast.makeText(this, id+"", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch(id) {
			default:
			case LOADER_TAGS:
				return new CursorLoader(this, Tags.URI, Tags.PROJECTION, null, null, null);
			case LOADER_ACCESS_POINTS:
				return new CursorLoader(this, AccessPoints.URI, AccessPoints.PROJECTION, null, null, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()) {
			default:
			case LOADER_TAGS:
				MatrixCursor extras = new MatrixCursor(new String[] { "_id", "name" });
				extras.addRow(new String[] { "-1", "All" });
				extras.addRow(new String[] { "-2", "Untagged" });
				Cursor tags = new MergeCursor(new Cursor[] {extras, cursor});
				mTagsAdapter.swapCursor(tags);
			break;
			case LOADER_ACCESS_POINTS:
				mAccessPointsAdapter.swapCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch(loader.getId()) {
			default:
			case LOADER_TAGS:
				mTagsAdapter.swapCursor(null);
			break;
			case LOADER_ACCESS_POINTS:
				mAccessPointsAdapter.swapCursor(null);
			break;
		}
	}
}
