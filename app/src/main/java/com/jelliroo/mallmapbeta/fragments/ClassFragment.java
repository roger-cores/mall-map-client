package com.jelliroo.mallmapbeta.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.activities.ClassesActivity;
import com.jelliroo.mallmapbeta.enums.ClassType;

/**
 * Created by roger on 2/19/2017.
 */

public class ClassFragment extends SuperClassFragment {


    public ClassFragment() {
        setClassType(ClassType.CLASS);
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
        super.onViewCreated(view, savedInstanceState);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_class);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_list_or_map){
            Toast.makeText(getContext(), "clicked and caught", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplayType(((ClassesActivity) getActivity()).getDisplayType());
    }
}
