package com.synthable.wifispy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TagsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private ListView mListView;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAdapter = new TempAdapter(getActivity(), mDataset);

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter);
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
}
