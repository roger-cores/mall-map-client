package com.jelliroo.mallmapbeta.vholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;

/**
 * Created by roger on 2/14/2017.
 */

public class ClassViewHolder extends RecyclerView.ViewHolder {

    public TextView className;

    public ClassViewHolder(View itemView) {
        super(itemView);
        className = (TextView) itemView.findViewById(R.id.class_name);
    }

    public String getClassName() {
        return className.getText().toString();
    }
}
