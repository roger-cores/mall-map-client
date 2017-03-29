package com.jelliroo.mallmapbeta.vholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;

/**
 * Created by roger on 2/20/2017.
 */

public class LaunchViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public View view;

    public LaunchViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        textView = (TextView) itemView.findViewById(R.id.class_name);
    }
}
