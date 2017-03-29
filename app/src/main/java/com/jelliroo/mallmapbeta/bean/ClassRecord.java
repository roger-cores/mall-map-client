package com.jelliroo.mallmapbeta.bean;

import com.jelliroo.mallmapbeta.enums.ClassType;

/**
 * Created by roger on 2/13/2017.
 */

public class ClassRecord {

    private String label;

    private float x;

    private float y;

    private ClassType classType;

    public ClassRecord(String label, float x, float y, ClassType classType) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return label;
    }
}
