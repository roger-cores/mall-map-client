package com.jelliroo.mallmapbeta.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.activities.ClassesActivity;
import com.jelliroo.mallmapbeta.enums.ClassType;

/**
 * Created by roger on 2/19/2017.
 */

public class HelperClassFragment extends SuperClassFragment {


    public HelperClassFragment() {
        this.setClassType(ClassType.HELPER_CLASS);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classes, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_class);




        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplayType(((ClassesActivity) getActivity()).getDisplayType());
    }
}
