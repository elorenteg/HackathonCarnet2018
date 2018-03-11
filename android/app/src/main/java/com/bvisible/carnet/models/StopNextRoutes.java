package com.bvisible.carnet.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class StopNextRoutes implements Comparable<StopNextRoutes> {
    private int stopid;
    private String stopname;
    private double lat;
    private double lng;
    private double distance;
    private ArrayList<RouteTime> routes;

    public int getStopid() {
        return stopid;
    }

    public void setStopid(int stopid) {
        this.stopid = stopid;
    }

    public String getStopname() {
        return stopname;
    }

    public void setStopname(String stopname) {
        this.stopname = stopname;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public ArrayList<RouteTime> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteTime> routes) {
        this.routes = routes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "StopNextRoutes{" +
                "stopid=" + stopid +
                ", stopname='" + stopname + '\'' +
                ", routes=" + routes +
                '}';
    }

    @Override
    public int compareTo(@NonNull StopNextRoutes snr) {
        if (this.distance <= snr.getDistance()) {
            return -1;
        }
        else return 1;
    }
}
