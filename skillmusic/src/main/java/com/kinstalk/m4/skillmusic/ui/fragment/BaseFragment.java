package com.kinstalk.m4.skillmusic.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected String TAG = getClass().getSimpleName();

    public BaseFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        this.mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
