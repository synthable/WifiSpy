package com.synthable.wifispy.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synthable.wifispy.R;

public class AccessPointsFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public AccessPointsFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_access_points, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
