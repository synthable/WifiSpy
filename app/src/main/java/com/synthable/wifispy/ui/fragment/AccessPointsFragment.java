package com.synthable.wifispy.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.synthable.wifispy.FragmentInteraction;
import com.synthable.wifispy.R;
import com.synthable.wifispy.WifiSpyService;
import com.synthable.wifispy.provider.DbContract;
import com.synthable.wifispy.provider.DbContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;

import java.util.HashSet;

public class AccessPointsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private FragmentInteraction.OnInteractionListener mListener;

    private static final int LOADER_ACCESS_POINTS = 0;

    private ListView mListView;
    private AccessPointsAdapter mAccessPointsAdapter;
    private FloatingActionButton mFloatingActionButton;

    private HashSet<Long> mSelectedApIds = new HashSet<>();

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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_access_points, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mFloatingActionButton.setImageResource(WifiSpyService.isRunning ? R.mipmap.ic_stop : R.mipmap.ic_play);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(WifiSpyService.isRunning) {
                    mFloatingActionButton.setImageResource(R.mipmap.ic_play);
                    WifiSpyService.stop(getActivity());
                } else {
                    mFloatingActionButton.setImageResource(R.mipmap.ic_stop);
                    WifiSpyService.start(getActivity());
                }
            }
        });

        mListView = getListView();
        setListAdapter(mAccessPointsAdapter);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int p, long i, boolean checked) {
                int checkedCount = mListView.getCheckedItemCount();
                actionMode.setSubtitle(checkedCount + " access points selected");

                //Buld a list of selected Tags to perform a delete on later
                Long id = mAccessPointsAdapter.getItemId(p);
                if (checked) {
                    mSelectedApIds.add(id);
                } else {
                    mSelectedApIds.remove(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.setTitle(getString(R.string.access_points_fragment_title));

                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.context_aps, menu);

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
                    case R.id.tag:
                        TagsFragment fragment = TagsFragment.newInstance(mSelectedApIds);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case R.id.delete:
                        /** TODO: Make this an async batch operation **/
                        for(Long id : mSelectedApIds) {
                            getActivity().getContentResolver().delete(
                                    AccessPoints.buildApUri(id), null, null
                            );
                        }
                        break;
                }

                actionMode.finish();

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mSelectedApIds.clear();
            }
        });

        if(mListener != null) {
            mListener.onFragemtnSetTitle(getString(R.string.access_points_fragment_title));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_access_points, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_tags:
                mListener.onFragemtnViewTags();
                break;
        }
        return super.onOptionsItemSelected(item);
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
                AccessPoints.Columns.SSID,
                AccessPoints.Columns.BSSID
        };
        private static final int[] TO = new int[] {
                R.id.ssid,
                R.id.bssid
        };

        public AccessPointsAdapter(Context context) {
            super(context, R.layout.list_access_point_item, null, FROM, TO);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            int strength = cursor.getInt(cursor.getColumnIndex(AccessPoints.Columns.STRENGHT)) * -1;
            ImageView image = (ImageView) view.findViewById(R.id.signal_strength_icon);

            String strengthImage = "low";
            if(strength <= 75) {
                strengthImage = "full";
            } else if (strength >= 76 && strength < 90) {
                strengthImage = "med";
            }

            image.setImageResource(
                context.getResources().getIdentifier(strengthImage, "mipmap", context.getPackageName())
            );
        }
    }
}
