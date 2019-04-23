package com.jelliroo.mallmapbeta.bean;


/**
 * Created by roger on 22/04/2019.
 */
public class Map {
    private String label;

    public Map(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Map{" +
                "label='" + label + '\'' +
                '}';
    }
}
