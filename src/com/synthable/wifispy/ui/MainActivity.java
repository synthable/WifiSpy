package com.synthable.wifispy.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.synthable.wifispy.R;
import com.synthable.wifispy.WifiSpyService;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPointTags;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.AccessPointsAdapter;
import com.synthable.wifispy.provider.adapter.TagsAdapter;
import com.synthable.wifispy.provider.model.AccessPointTag;

public class MainActivity extends ListActivity implements
	ActionBar.OnNavigationListener,
	LoaderManager.LoaderCallbacks<Cursor>,
	MultiChoiceModeListener {

	private static final int LOADER_TAGS = 0;
	private static final int LOADER_ACCESS_POINTS = 1;
	private static final int LOADER_ACCESS_POINTS_TAG = 2;

	private static final int TAG_PICKER = 0;

	private ActionBar mActionBar;

	private TagsAdapter mTagsAdapter;
	private AccessPointsAdapter mAccessPointsAdapter;

	private ArrayList<Long> mCheckedIds = new ArrayList<Long>();

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
		getListView().setMultiChoiceModeListener(this);

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
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == TAG_PICKER) {
			if(resultCode == RESULT_OK) {
				long[] ids = data.getLongArrayExtra("tags");
				if(ids.length != 0) {
					for(long accessPointId : mCheckedIds) {
						for(long id : ids) {
							AccessPointTag apt = new AccessPointTag(accessPointId, id);
							getContentResolver().insert(AccessPointTags.URI, apt.toContentValues());
						}
					}
				}
			}
		}
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
		Bundle bundle = new Bundle();
		switch((int)id) {
			case -1:
				getLoaderManager().restartLoader(LOADER_ACCESS_POINTS, null, this);
			break;
			case -2:
			break;
			default:
				bundle.putLong(Tags.Columns._ID, id);
				getLoaderManager().restartLoader(LOADER_ACCESS_POINTS_TAG, bundle, this);
			break;
		}
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
			case LOADER_ACCESS_POINTS_TAG:
				long tagId = (Long) args.get(Tags.Columns._ID);
				return new CursorLoader(this, Tags.buildTagApsUri(tagId), AccessPoints.PROJECTION, null, null, null);
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
			case LOADER_ACCESS_POINTS_TAG:
				mAccessPointsAdapter.swapCursor(cursor);
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
			case LOADER_ACCESS_POINTS_TAG:
				mAccessPointsAdapter.swapCursor(null);
			break;
			case LOADER_ACCESS_POINTS:
				mAccessPointsAdapter.swapCursor(null);
			break;
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
            	for(long id : mCheckedIds) {
            		getContentResolver().delete(AccessPoints.buildApUri(id), null, null);
            	}
                mode.finish();
                return true;
            case R.id.action_tag:
	        	Intent intent = new Intent(Intent.ACTION_PICK, null, this, TagsActivity.class);
	        	intent.setType(Tags.CONTENT_ITEM_TYPE);
	            startActivityForResult(intent, TAG_PICKER);
	            return true;
            default:
                return false;
        }
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.access_points_context, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mCheckedIds = new ArrayList<Long>();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		mode.setTitle(getListView().getCheckedItemCount() + " Selected");
		if(checked) {
			mCheckedIds.add(id);
		} else {
			mCheckedIds.remove(id);
		}
	}
}
