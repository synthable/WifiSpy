package com.synthable.wifispy.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.synthable.wifispy.FragmentInteraction;
import com.synthable.wifispy.R;
import com.synthable.wifispy.provider.DbContract.AccessPoints;

public class AccessPointsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private FragmentInteraction.OnInteractionListener mListener;

    private static final int LOADER_ACCESS_POINTS = 0;

    private ListView mListView;
    private AccessPointsAdapter mAccessPointsAdapter;

    public AccessPointsFragment() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteraction.OnInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccessPointsAdapter = new AccessPointsAdapter(getContext());

        getLoaderManager().initLoader(LOADER_ACCESS_POINTS, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_access_points, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = getListView();
        setListAdapter(mAccessPointsAdapter);

        if(mListener != null) {
            mListener.onFragemtnSetTitle(getString(R.string.taccess_points_fragment_title));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), AccessPoints.URI, AccessPoints.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAccessPointsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAccessPointsAdapter.swapCursor(null);
    }

    public static class AccessPointsAdapter extends SimpleCursorAdapter {

        private static final String[] FROM = new String[] {
                AccessPoints.Columns.SSID
        };
        private static final int[] TO = new int[] {
                R.id.ssid
        };

        public AccessPointsAdapter(Context context) {
            super(context, R.layout.list_access_point_item, null, FROM, TO);
        }
    }
}
