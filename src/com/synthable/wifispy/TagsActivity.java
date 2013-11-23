package com.synthable.wifispy;

import com.synthable.wifispy.provider.Tag;
import com.synthable.wifispy.provider.WifiSpyContract.Tags;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class TagsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tags);

		Cursor cursor = getContentResolver().query(Tags.URI, Tags.PROJECTION, null, null, null);
		startManagingCursor(cursor);

		TagsAdapter adapter = new TagsAdapter(this, cursor);

		getListView().setAdapter(adapter);
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
}
