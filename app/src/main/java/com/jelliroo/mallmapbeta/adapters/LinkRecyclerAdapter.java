package com.jelliroo.mallmapbeta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.Link;
import com.jelliroo.mallmapbeta.vholders.LinkViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by roger on 2/19/2017.
 */

public class LinkRecyclerAdapter extends RecyclerView.Adapter<LinkViewHolder> {

    LinkedHashMap<Integer, Link> links = new LinkedHashMap<>();

    @Override
    public LinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, null));
    }

    @Override
    public void onBindViewHolder(LinkViewHolder holder, int position) {
        Link link = getItem(position);
        holder.linkName.setText(link.getSourceLabel().getLabel() + " to " + link.getDestinationLabel().getLabel());
        holder.distance.setText("" + link.getDistance());
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    @Override
    public long getItemId(int position) {
        return links.get(position).getIdentifier();
    }

    public Link getItem(int position){
        return new ArrayList<>(links.values()).get(position);
    }

    public void addAll(List<Link> linkList){
        for(Link link : linkList){
            links.put(link.getIdentifier(), link);
        }
    }

    public void clear(){
        links.clear();
    }

    public void addItem(Link link){
        links.put(link.getIdentifier(), link);
    }

    public void removeItem(int identifier){
        links.remove(identifier);
    }
}
