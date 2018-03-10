package com.bvisible.carnet.models;

public class BikeLane {
    private String id;
    private String name;
    private String lat1;
    private String lng1;
    private String lat2;
    private String lng2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat1() {
        return lat1;
    }

    public void setLat1(String lat1) {
        this.lat1 = lat1;
    }

    public String getLng1() {
        return lng1;
    }

    public void setLng1(String lng1) {
        this.lng1 = lng1;
    }

    public String getLat2() {
        return lat2;
    }

    public void setLat2(String lat2) {
        this.lat2 = lat2;
    }

    public String getLng2() {
        return lng2;
    }

    public void setLng2(String lng2) {
        this.lng2 = lng2;
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
}
