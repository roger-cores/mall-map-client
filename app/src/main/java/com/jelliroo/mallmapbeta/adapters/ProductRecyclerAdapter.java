package com.jelliroo.mallmapbeta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.Link;
import com.jelliroo.mallmapbeta.bean.Product;
import com.jelliroo.mallmapbeta.vholders.LinkViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by roger on 2/19/2017.
 */

public class ProductRecyclerAdapter extends RecyclerView.Adapter<LinkViewHolder> {

    LinkedHashMap<Integer, Product> products = new LinkedHashMap<>();

    @Override
    public LinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, null));
    }

    @Override
    public void onBindViewHolder(LinkViewHolder holder, int position) {
        Product product = getItem(position);
        holder.linkName.setText(product.getProduct_name());
        holder.distance.setText("Rs. " + product.getProduct_cost());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getIdentifier();
    }

    public Product getItem(int position){
        return new ArrayList<>(products.values()).get(position);
    }

    public void addAll(List<Product> productList){
        for(Product product : productList){
            products.put(product.getIdentifier(), product);
        }
    }

    public void clear(){
        products.clear();
    }

    public void addItem(Product product){
        products.put(product.getIdentifier(), product);
    }

    public void removeItem(int identifier){
        products.remove(identifier);
    }
}
