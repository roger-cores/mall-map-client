package com.jelliroo.mallmapbeta.bean;

/**
 * Created by roger on 2/19/2017.
 */

public class Link {

    private int identifier;

    private int distance;

    private ClassRecord sourceLabel;

    private ClassRecord destinationLabel;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ClassRecord getSourceLabel() {
        return sourceLabel;
    }

    public void setSourceLabel(ClassRecord sourceLabel) {
        this.sourceLabel = sourceLabel;
    }

    public ClassRecord getDestinationLabel() {
        return destinationLabel;
    }

    public void setDestinationLabel(ClassRecord destinationLabel) {
        this.destinationLabel = destinationLabel;
    }
}
