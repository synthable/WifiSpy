package com.synthable.wifispy.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.DbContract;
import com.synthable.wifispy.provider.DbContract.Tags;
import com.synthable.wifispy.provider.model.Tag;

import java.util.HashSet;

public class TagsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        void onFragemtnSetTitle(String title);
    }

    private static final int LOADER_TAGS = 0;

    private FloatingActionButton mFloatingActionButton;
    private ListView mListView;
    private TagsAdapter mTagsAdapter;

    private HashSet<Long> mSelectedTagIds = new HashSet<>();

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

        mListView = getListView();
        mTagsAdapter = new TagsAdapter(getContext());
        setListAdapter(mTagsAdapter);

        getLoaderManager().initLoader(LOADER_TAGS, null, this);

        if(mListener != null) {
            mListener.onFragemtnSetTitle(getString(R.string.tags_fragment_title));
        }

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int p, long i, boolean checked) {
                int checkedCount = mListView.getCheckedItemCount();
                actionMode.setSubtitle(checkedCount + " tags selected");

                // Only show the "edit" option if not more than one item is selected
                actionMode.getMenu().getItem(0).setVisible(checkedCount == 1);

                //Buld a list of selected Tags to perform a delete on later
                Long id = mTagsAdapter.getItemId(p);
                if (checked) {
                    mSelectedTagIds.add(id);
                } else {
                    mSelectedTagIds.remove(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle(getString(R.string.tags_fragment_title));

                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.context_tags, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    default:
                    case R.id.edit:
                        Long tagId = mSelectedTagIds.iterator().next().longValue();
                        EditTagDialog dialog = EditTagDialog.newInstance(tagId);
                        dialog.show(getFragmentManager(), null);
                        break;
                    case R.id.delete:
                        /**
                         * Make sure the user cannot delete the default tag and
                         * tell them why it's not being removed
                         */
                        if(mSelectedTagIds.contains(1L)) {
                            mSelectedTagIds.remove(1L);
                            mListView.setItemChecked(0, false);

                            Snackbar.make(mListView, R.string.tags_default_remove, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    })
                                    .show();
                        }

                        /** TODO: Make this an async batch operation **/
                        for(Long id : mSelectedTagIds) {
                            getActivity().getContentResolver().delete(
                                    Tags.buildTagUri(id), null, null
                            );
                        }
                        break;
                }

                actionMode.finish();

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mSelectedTagIds.clear();
            }
        });
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

    public static class BaseTagDialog extends DialogFragment implements
            DialogInterface.OnClickListener {

        protected View mDialogLayout;
        protected EditText mTagInput;
        protected Long mTagId;
        protected AlertDialog.Builder mDialogBuilder;

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            mDialogLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_tag, null);
            mTagInput = (EditText) mDialogLayout.findViewById(R.id.tags_new_input);

            return mDialogBuilder
                    .setView(mDialogLayout)
                    .setNegativeButton(android.R.string.cancel, this)
                    .setPositiveButton(android.R.string.ok, this)
                    .create();
        }

        @Override
        public void onResume() {
            super.onResume();
            mTagInput.post(new Runnable() {
                @Override
                public void run() {
                    mTagInput.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mTagInput, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int whichButton) {
            if(whichButton == DialogInterface.BUTTON_POSITIVE) {
                Tag tag = new Tag();
                tag.setId(mTagId);
                tag.setName(mTagInput.getText().toString());
                getActivity().getContentResolver().insert(Tags.URI, tag.toContentValues());
            }

            dismiss();
        }
    }

    public static class AddTagDialog extends BaseTagDialog {

        protected static final String TITLE = "Edit Tag Name";

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            mDialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle(TITLE);

            return super.onCreateDialog(savedInstanceState);
        }
    }

    public static class EditTagDialog extends BaseTagDialog {

        protected static final String TITLE = "Edit Tag Name";

        public static EditTagDialog newInstance(Long id) {
            Bundle args = new Bundle();
            args.putLong(Tags.Columns._ID, id);

            EditTagDialog dialog = new EditTagDialog();
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            mTagId = getArguments().getLong(Tags.Columns._ID);
            mDialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle(TITLE);

            return super.onCreateDialog(savedInstanceState);
        }
    }
}
