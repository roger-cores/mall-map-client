package com.jelliroo.mallmapbeta.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.enums.ClassType;

/**
 * Created by roger on 2/19/2017.
 */

public class SuperClassFragment extends Fragment {


    protected FrameLayout frameLayout;

    private ClassType classType = ClassType.CLASS;

    /**
     * true: map
     * false: list
     */
    private boolean displayType = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDisplayType(displayType);
    }

    public void setDisplayType(boolean displayType) {
        this.displayType = displayType;
        Fragment fragment;
        Bundle args = new Bundle();
        args.putString("classType", classType.name());

        if(displayType){
            fragment = new ClassesMapFragment();
        } else {
            fragment = new ClassListFragment();
        }
        fragment.setArguments(args);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_class, fragment)
                .commit();
    }

    @Override
    public boolean getAllowEnterTransitionOverlap() {
        return super.getAllowEnterTransitionOverlap();
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;



    }

    public ClassType getClassType() {
        return classType;
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }
}
