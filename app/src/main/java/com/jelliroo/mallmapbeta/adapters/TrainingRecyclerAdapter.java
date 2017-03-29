package com.jelliroo.mallmapbeta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.ClassCountResponse;
import com.jelliroo.mallmapbeta.bean.Product;
import com.jelliroo.mallmapbeta.vholders.LinkViewHolder;
import com.jelliroo.mallmapbeta.vholders.TrainingViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by roger on 2/19/2017.
 */

public class TrainingRecyclerAdapter extends RecyclerView.Adapter<TrainingViewHolder> {

    LinkedHashMap<String, ClassCountResponse> classes = new LinkedHashMap<>();

    @Override
    public TrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrainingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, null));
    }

    @Override
    public void onBindViewHolder(TrainingViewHolder holder, int position) {
        ClassCountResponse classInstance = getItem(position);
        holder.linkName.setText(classInstance.getClassRecordLabel());
        holder.distance.setText(classInstance.getClass_count());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public ClassCountResponse getItem(int position){
        return new ArrayList<>(classes.values()).get(position);
    }

    public void addAll(List<ClassCountResponse> classInstanceList){
        for(ClassCountResponse classInstance : classInstanceList){
            classes.put(classInstance.getClassRecordLabel(), classInstance);
        }
    }

    public void clear(){
        classes.clear();
    }

    public void addItem(ClassCountResponse classInstance){
        classes.put(classInstance.getClassRecordLabel(), classInstance);
    }

    public void removeItem(String className){
        classes.remove(className);
    }
}
