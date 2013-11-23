package com.synthable.wifispy;

import com.synthable.wifispy.provider.Tag;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class TagsActivity extends ListActivity implements
	LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_TAGS = 0;

	TagsAdapter mTagsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);

		mTagsAdapter = new TagsAdapter(this, null);
		getListView().setAdapter(mTagsAdapter);

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
}
