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
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.Map;
import com.jelliroo.mallmapbeta.vholders.LaunchViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by roger on 2/20/2017.
 */

public class LaunchRecyclerAdapter extends RecyclerView.Adapter<LaunchViewHolder> {

    private LinkedHashMap<String, Map> maps = new LinkedHashMap<>();
    Activity activity;

    @Override
    public LaunchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LaunchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, null));
    }

    @Override
    public void onBindViewHolder(final LaunchViewHolder holder, int position) {
        Map map = new ArrayList<>(maps.values()).get(position);
        holder.textView.setText(map.getLabel());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity == null) return;

                Intent intent;
                intent = new Intent(activity, MainActivity.class);

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setMaps(LinkedHashMap<String, Map> maps) {
        this.maps = maps;
    }

    public void addMap(Map map) {
        this.maps.put(map.getLabel(), map);
    }

    public void removeMap(String key) {
        this.maps.remove(key);
    }

    public void clear() {
        maps.clear();
    }
}
