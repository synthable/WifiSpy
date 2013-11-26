package com.synthable.wifispy.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.EditText;

import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.TagsAdapter;
import com.synthable.wifispy.provider.model.Tag;

public class TagsActivity extends ListActivity implements
	LoaderManager.LoaderCallbacks<Cursor>,
	MultiChoiceModeListener {

	private static final int LOADER_TAGS = 0;

	private TagsAdapter mTagsAdapter;
	private ArrayList<Long> mCheckedIds = new ArrayList<Long>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);

		mTagsAdapter = new TagsAdapter(this, null);
		getListView().setAdapter(mTagsAdapter);
		getListView().setMultiChoiceModeListener(this);

		getLoaderManager().initLoader(LOADER_TAGS, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tags, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_tags_add:
				new AddTagDialog().show(getFragmentManager(), null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class AddTagDialog extends DialogFragment {

		private static final String TITLE = "Add a Tag";

		private EditText mNewTagInput;

		@Override
		public Dialog onCreateDialog(final Bundle savedInstanceState) {
			View view = getActivity().getLayoutInflater().inflate(R.layout.add_tag, null);
			mNewTagInput = (EditText) view.findViewById(R.id.tags_new_input);

			return new AlertDialog.Builder(getActivity())
	            .setTitle(TITLE)
	            .setView(view)
	            .setNegativeButton(android.R.string.cancel,
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	dismiss();
	                    }
	                }
	            )
	            .setPositiveButton(android.R.string.ok,
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	Tag tag = new Tag();
	                    	tag.setName(mNewTagInput.getText().toString());
	                    	getActivity().getContentResolver().insert(Tags.URI, tag.toContentValues());
	                    	dismiss();
	                    }
	                }
	            )
	            .create();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch(id) {
			default:
			case LOADER_TAGS:
				return new CursorLoader(this, Tags.URI, Tags.PROJECTION, null, null, null);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch(loader.getId()) {
			default:
			case LOADER_TAGS:
				mTagsAdapter.swapCursor(cursor);
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
		}
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
            	for(long id : mCheckedIds) {
            		getContentResolver().delete(Tags.buildTagUri(id), null, null);
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
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// Here you can perform updates to the CAB due to
        // an invalidate() request
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
