package com.jelliroo.mallmapbeta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.Beacon;
import com.jelliroo.mallmapbeta.vholders.BeaconViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by roger on 2/14/2017.
 */

public class BeaconRecyclerAdapter extends RecyclerView.Adapter<BeaconViewHolder> {

    LinkedHashMap<String, Beacon> beaconLinkedHashMap;

    public BeaconRecyclerAdapter(LinkedHashMap<String, Beacon> beaconLinkedHashMap) {
        this.beaconLinkedHashMap = beaconLinkedHashMap;
    }

    public BeaconRecyclerAdapter() {
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_item_recycler, null);
        return new BeaconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        Beacon beacon = new ArrayList<>(beaconLinkedHashMap.values()).get(position);
        if(beacon.getStrength() == null){
            holder.beaconName.setText(beacon.getName());
            holder.beaconRssiSsid.setText(beacon.getMac());
            holder.beaconRssi.setText("");
            holder.beaconMac.setText("");
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.beaconName.setText(beacon.getName());
            holder.beaconRssiSsid.setText(beacon.getSsid());
            holder.beaconRssi.setText(beacon.getStrength() + "");
            holder.beaconMac.setText(beacon.getMac());
            holder.imageView.setVisibility(View.VISIBLE);
            if(beacon.getType() == Beacon.BLUETOOTH){
                holder.imageView.setImageResource(R.drawable.ic_bluetooth_black_24dp);
            } else if(beacon.getType() == Beacon.WIFI){
                holder.imageView.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_24dp);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            }

        }

    }

    @Override
    public int getItemCount() {
        if(beaconLinkedHashMap == null)
            return 0;
        else {
            return beaconLinkedHashMap.size();
        }
    }

    @Override
    public long getItemId(int position) {
        if(beaconLinkedHashMap == null)
            return 0;
        else {
            return new ArrayList<>(beaconLinkedHashMap.keySet()).get(position).hashCode();
        }
    }

    public void setBeaconLinkedHashMap(LinkedHashMap<String, Beacon> beaconLinkedHashMap) {
        this.beaconLinkedHashMap = beaconLinkedHashMap;
    }

    public LinkedHashMap<String, Beacon> getBeaconLinkedHashMap() {
        return beaconLinkedHashMap;
    }

    public void addBeacon(Beacon beacon){
        if(beaconLinkedHashMap != null)
            beaconLinkedHashMap.put(beacon.getMac(), beacon);
    }

    public void removeBeacon(String name){
        if(beaconLinkedHashMap != null)
            beaconLinkedHashMap.remove(name);
    }

    public void addAll(Collection<Beacon> beacons){
        if(beaconLinkedHashMap != null){
            for(Beacon beacon : beacons){
                beaconLinkedHashMap.put(beacon.getMac(), beacon);
            }
        }
    }

    public Beacon getItem(String mac){
        if(beaconLinkedHashMap != null){
            return beaconLinkedHashMap.get(mac);
        } else return null;
    }


    public void clear() {
        if(beaconLinkedHashMap != null)
            beaconLinkedHashMap.clear();
    }

    public void resetStrengths(int type) {
        for(Beacon beacon : beaconLinkedHashMap.values()){
            if(beacon.getType() == null) continue;
            if(beacon.getType() == type){
                beacon.setStrength(0);
            }
        }
    }

    public void resetStrengths(int type, List<String> macs){
        for(Beacon beacon : beaconLinkedHashMap.values()){
            if(beacon.getType() == null) continue;
            if(beacon.getType() == type && !macs.contains(beacon.getMac())){
                beacon.setStrength(0);
            }
        }
    }
}
