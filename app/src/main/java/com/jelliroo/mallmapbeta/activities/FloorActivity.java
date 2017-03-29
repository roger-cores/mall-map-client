package com.jelliroo.mallmapbeta.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.LaunchRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FloorActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        recyclerView = (RecyclerView) findViewById(R.id.launch_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        LaunchRecyclerAdapter adapter = new LaunchRecyclerAdapter();
        adapter.setActivity(this);

        List<String> buildings = new ArrayList<>();
        buildings.add("BASE 4");
        buildings.add("BASE 3");
        buildings.add("BASE 2");
        buildings.add("BASE 1");
        buildings.add("FLOOR 1");
        buildings.add("FLOOR 2");
        buildings.add("FLOOR 3");
        buildings.add("FLOOR 4");
        adapter.setBuildings(buildings);

        recyclerView.setAdapter(adapter);


    }
}
