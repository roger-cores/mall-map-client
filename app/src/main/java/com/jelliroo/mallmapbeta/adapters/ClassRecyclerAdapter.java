package com.jelliroo.mallmapbeta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.vholders.ClassViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by roger on 2/14/2017.
 */

public class ClassRecyclerAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    private  LinkedHashMap<String, ClassRecord> classRecordLinkedHashMap = new LinkedHashMap<>();
    private int layoutId;

    public ClassRecyclerAdapter(){

    }

    public ClassRecyclerAdapter(int layoutId){
        this.layoutId = layoutId;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        ClassRecord classRecord = new ArrayList<>(classRecordLinkedHashMap.values()).get(position);
        holder.className.setText(classRecord.getLabel());
    }

    @Override
    public int getItemCount() {
        return classRecordLinkedHashMap.size();
    }

    public void removeClass(String name){
        classRecordLinkedHashMap.remove(name);
    }

    public void addClass(ClassRecord classRecord){
        classRecordLinkedHashMap.put(classRecord.getLabel(), classRecord);
    }

    public void clear(){
        classRecordLinkedHashMap.clear();
    }
}
