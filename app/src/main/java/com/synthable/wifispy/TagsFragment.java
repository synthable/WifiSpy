package com.synthable.wifispy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.synthable.wifispy.provider.DbContract;
import com.synthable.wifispy.provider.DbContract.Tags;

public class TagsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        void onFragemtnSetTitle(String title);
    }

    private static final int LOADER_TAGS = 0;

    private FloatingActionButton mFloatingActionButton;
    private TagsAdapter mTagsAdapter;

    public TagsFragment() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddTagDialog().show(getFragmentManager(), null);
            }
        });

        mTagsAdapter = new TagsAdapter(getContext());
        setListAdapter(mTagsAdapter);

        getLoaderManager().initLoader(LOADER_TAGS, null, this);

        if(mListener != null) {
            mListener.onFragemtnSetTitle(getString(R.string.tags_fragment_title));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), Tags.URI, Tags.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mTagsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTagsAdapter.swapCursor(null);
    }

    public static class TagsAdapter extends SimpleCursorAdapter {

        private static final String[] FROM = new String[] {
                DbContract.Tags.Columns.NAME
        };
        private static final int[] TO = new int[] {
                R.id.tag_name
        };

        public TagsAdapter(Context context) {
            super(context, R.layout.list_tag_item, null, FROM, TO);
        }
    }

    public static class AddTagDialog extends DialogFragment implements
            DialogInterface.OnClickListener {

        private static final String TITLE = "Create a New Tag";

        private EditText mNewTagInput;

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_tag, null);
            mNewTagInput = (EditText) view.findViewById(R.id.tags_new_input);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(TITLE)
                    .setView(view)
                    .setNegativeButton(android.R.string.cancel, this)
                    .setPositiveButton(android.R.string.ok, this)
                    .create();
        }

        @Override
        public void onResume() {
            super.onResume();
            mNewTagInput.post(new Runnable() {
                @Override
                public void run() {
                    mNewTagInput.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mNewTagInput, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int whichButton) {
            if(whichButton == DialogInterface.BUTTON_POSITIVE) {
                Tag tag = new Tag();
                tag.setName(mNewTagInput.getText().toString());
                getActivity().getContentResolver().insert(Tags.URI, tag.toContentValues());
            }

            dismiss();
        }
    }
}
