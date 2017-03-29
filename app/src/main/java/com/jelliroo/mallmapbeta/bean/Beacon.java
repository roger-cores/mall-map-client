package com.jelliroo.mallmapbeta.bean;

/**
 * Created by roger on 2/14/2017.
 */

public class Beacon {

    public static int BLUETOOTH = 0, WIFI = 1;

    private String name;

    private String mac;

    private Integer strength;

    private String ssid;

    private Integer type;

    public Beacon(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public Beacon(String name, String mac, int strength, String ssid, int type) {
        this.name = name;
        this.mac = mac;
        this.strength = strength;
        this.ssid = ssid;
        this.type = type;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Beacon clone() {
        return new Beacon(name, mac, strength, ssid, type);
    }
}
