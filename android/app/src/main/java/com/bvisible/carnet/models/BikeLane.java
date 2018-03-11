package com.bvisible.carnet.models;

import android.support.annotation.NonNull;

public class BikeLane implements Comparable<BikeLane>{
    private String id;
    private String cid;
    private String name;
    private double lat1;
    private double lng1;
    private double lat2;
    private double lng2;
    private double distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLng1() {
        return lng1;
    }

    public void setLng1(double lng1) {
        this.lng1 = lng1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLng2() {
        return lng2;
    }

    public void setLng2(double lng2) {
        this.lng2 = lng2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "BikeLane{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lat1='" + lat1 + '\'' +
                ", lng1='" + lng1 + '\'' +
                ", lat2='" + lat2 + '\'' +
                ", lng2='" + lng2 + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull BikeLane snr) {
        if (this.distance <= snr.getDistance()) {
            return -1;
        }
        else return 1;
    }
}
