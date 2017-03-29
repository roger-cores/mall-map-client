package com.jelliroo.mallmapbeta.vholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;

/**
 * Created by roger on 2/19/2017.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public TextView linkName, distance;

    public ProductViewHolder(View itemView) {
        super(itemView);
        linkName = (TextView) itemView.findViewById(R.id.link_name);
        distance = (TextView) itemView.findViewById(R.id.link_distance);
    }
}
