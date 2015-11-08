package com.synthable.wifispy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class TagsFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private TempAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;

    private String[] mDataset = {
            "String One", "String Two", "String Three",
            "String Four", "String Five", "String Six",
            "String Seven", "String Eight", "String Nine",
            "String Ten", "String Eleven", "String Twelve",
            "String Thirteen", "String Fourteen", "String Fifteen",
            "String Sixteen", "String Seventeen", "String Eighteen"
    };

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

        mAdapter = new TempAdapter(getActivity(), mDataset);
        setListAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public static class TempAdapter extends ArrayAdapter<String> {
        public TempAdapter(Context context, String[] mDataset) {
            super(context, R.layout.list_tags_item, mDataset);
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
                /*Tag tag = new Tag();
                tag.setName(mNewTagInput.getText().toString());
                getActivity().getContentResolver().insert(Tags.URI, tag.toContentValues());*/
            }

            dismiss();
        }
    }
}
