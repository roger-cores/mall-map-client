package com.jelliroo.mallmapbeta.vholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;

/**
 * Created by roger on 2/14/2017.
 */

public class BeaconViewHolder extends RecyclerView.ViewHolder {

    public TextView beaconName, beaconRssiSsid, beaconRssi, beaconMac;
    public ImageView imageView;

    public BeaconViewHolder(View itemView) {
        super(itemView);
        beaconName = (TextView) itemView.findViewById(R.id.beacon_name);
        beaconRssiSsid = (TextView) itemView.findViewById(R.id.beacon_rssi_ssid);
        beaconRssi = (TextView) itemView.findViewById(R.id.beacon_rssi);
        beaconMac = (TextView) itemView.findViewById(R.id.beacon_mac);
        imageView = (ImageView) itemView.findViewById(R.id.imageView2);
    }

    public String getName(){
        return  beaconName.getText().toString();
    }



}
