package com.jelliroo.mallmapbeta.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.activities.FloorActivity;
import com.jelliroo.mallmapbeta.activities.LaunchActivity;
import com.jelliroo.mallmapbeta.activities.MainActivity;
import com.jelliroo.mallmapbeta.vholders.LaunchViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roger on 2/20/2017.
 */

public class LaunchRecyclerAdapter extends RecyclerView.Adapter<LaunchViewHolder> {

    List<String> buildings = new ArrayList<>();
    Activity activity;

    @Override
    public LaunchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LaunchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, null));
    }

    @Override
    public void onBindViewHolder(final LaunchViewHolder holder, int position) {
        holder.textView.setText(buildings.get(position));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity == null) return;

                Intent intent;

                if(activity instanceof LaunchActivity){
                    intent = new Intent(activity, FloorActivity.class);
                } else {
                    intent = new Intent(activity, MainActivity.class);

                }

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setBuildings(List<String> buildings) {
        this.buildings = buildings;
    }
}
