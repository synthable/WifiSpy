package com.synthable.wifispy.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.EditText;
import android.widget.ListView;

import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;
import com.synthable.wifispy.provider.adapter.TagsAdapter;
import com.synthable.wifispy.provider.model.Tag;

public class TagsActivity extends ListActivity implements
	LoaderManager.LoaderCallbacks<Cursor>,
	MultiChoiceModeListener {

	private static final int LOADER_TAGS = 0;

	private static final int TAG_PICKER = 0;

	private TagsAdapter mTagsAdapter;
	private ArrayList<Long> mCheckedIds = new ArrayList<Long>();
	private ActionBar mActionBar;
	private LayoutInflater mInflater;
	private View mContextMenuView;
	private String mMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);

		mMode = getIntent().getAction();

		mActionBar = getActionBar();
		mTagsAdapter = new TagsAdapter(this, null);
		getListView().setAdapter(mTagsAdapter);
		if(mMode == Intent.ACTION_PICK) getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setMultiChoiceModeListener(this);

		getLoaderManager().initLoader(LOADER_TAGS, null, this);

		if(mMode == Intent.ACTION_PICK) {
			mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			mContextMenuView = mInflater.inflate(R.layout.action_bar_custom_view_done_cancel, null);
	        mContextMenuView.findViewById(R.id.actionbar_done)
		        .setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		            	long[] ids = getListView().getCheckedItemIds();
						Intent intent = new Intent();
						intent.putExtra("tags", ids);
						setResult(RESULT_OK, intent);
		                finish();
		            }
		        });
	        mContextMenuView.findViewById(R.id.actionbar_cancel)
		        .setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                finish();
		            }
		        });

	        mActionBar.setDisplayOptions(
	        	ActionBar.DISPLAY_SHOW_CUSTOM,
	        	ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
	        );
	        mActionBar.setCustomView(mContextMenuView,
	            new ActionBar.LayoutParams(
	            	ViewGroup.LayoutParams.MATCH_PARENT,
	            	ViewGroup.LayoutParams.MATCH_PARENT
	            )
	        );
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(mMode != Intent.ACTION_PICK) getMenuInflater().inflate(R.menu.tags, menu);
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

		@Override
		public void onResume() {
			super.onResume();
			mNewTagInput.post(new Runnable() {
				@Override
				public void run() {
					mNewTagInput.requestFocusFromTouch();
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mNewTagInput, InputMethodManager.SHOW_IMPLICIT);
				}
			});
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
		mode.setCustomView(mContextMenuView);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
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
