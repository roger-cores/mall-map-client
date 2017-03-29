package com.jelliroo.mallmapbeta.bean;

/**
 * Created by roger on 2/6/2017.
 */

public class ScanResult {

    private String SSID;

    private String BSSID;

    private int strength;

    public ScanResult(String SSID, String BSSID, int strength) {
        this.SSID = SSID;
        this.strength = strength;
        this.BSSID = BSSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
