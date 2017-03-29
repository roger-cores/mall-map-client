package com.jelliroo.mallmapbeta.bean;

import android.graphics.PointF;

import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.enums.PinType;

/**
 * Created by roger on 2/13/2017.
 */

public class MapPin {
    float X, Y;
    int id;

    String className;

    PinType pinType;

    ClassType classType;

    public MapPin(float X, float Y, int id, PinType pinType, String className, ClassType classType) {
        this.X = X;
        this.Y = Y;
        this.id = id;
        this.pinType = pinType;
        this.className = className;
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public PinType getPinType() {
        return pinType;
    }

    public void setPinType(PinType pinType) {
        this.pinType = pinType;
    }

    public float getX() {
        return X;
    }

    public void setX(float X) {
        this.X = X;
    }

    public float getY() {
        return Y;
    }

    public void setY(float Y) {
        this.Y = Y;
    }

    public PointF getPoint() {
        return new PointF(this.X, this.Y);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}